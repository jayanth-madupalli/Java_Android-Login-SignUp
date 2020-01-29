<?php 
    include_once "db.php";

    class User{
        
        private $db;

        private $db_table = "users";

        public function __construct(){
            $this->db = new Db();
        }

        public function isValidUser($user, $pass){
            $q = mysqli_fetch_assoc(mysqli_query($this->db->getDb(), "SELECT salt from users where username='$user' "));
            if($q['salt']){
            $pass = sha1($pass.md5($q['salt']));
            $query = "SELECT * from ".$this->db_table." WHERE username = '$user' AND pass = '$pass' limit 1";
            $res = mysqli_query($this->db->getDb(), $query);

            if(mysqli_num_rows($res) == 1){
                mysqli_close($this->db->getDb());
                return true;
            }
            }
            mysqli_close($this->db->getDb());
            return false; 
        }
        
        public function isEmailUserExist($user, $email){
            $query = "SELECT * FROM ".$this->db_table." WHERE username = '$user' or email = '$email' ";
            $res = mysqli_query($this->db->getDb(), $query);
            if(mysqli_num_rows($res) > 0){
                mysqli_close($this->db->getDb());
                return true;
            }
            return false; 
        }

        public function isValidEmail($email){
            return filter_var($email, FILTER_VALIDATE_EMAIL);
        }

        public function createUser($user, $pass, $email){
            $isExisting = $this->isEmailUserExist($user, $email);
            $json = array();
            if($isExisting){
                $json['success'] = 0;
                $json['message'] = "Username or Email already exists, Please try again.";
            }else{
                $isValid = $this->isValidEmail($email);
                if($isValid){
                    $salt = rand(10000000,999999999);
                    $pass = sha1($pass.md5($salt));
                    $query = "INSERT INTO ".$this->db_table." (username, pass, salt, email, created_at, updated_at) VALUES ('$user', '$pass', '$salt', '$email', NOW(), NOW())";
                    $ins = mysqli_query($this->db->getDb(), $query);

                    if($ins == 1){
                        $json['success'] = 1;
                        $json['message'] = "Successfully registered, Please proceed to login.";
                    }else{
                        $json['success'] = 0;
                        $json['message'] = "Error in registration, Please try agian.";
                    }
        
                }else{
                    $json['success'] = 0;
                    $json['message'] = "Invalid email, Please try again.";
                }

            }
            mysqli_close($this->db->getDb());
            return $json;
        }

        public function loginUser($user, $pass){
            $json = array();

            $isValid = $this->isValidUser($user, $pass);
            if($isValid){
                $json['success'] = 1;
                $json['message'] = "Succesfully logged in.";
            }else{
                $json['success'] = 0;
                $json['message'] = "Invalid username/password.";
            }

            return $json;
        }
    }
?>