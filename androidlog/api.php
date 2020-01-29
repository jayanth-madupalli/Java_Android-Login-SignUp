<?php
    require_once "user.php";

    $user = "";
    $pass = "";
    $email = "";

    if(isset($_POST['user']))
        $user = $_POST['user'];
    if(isset($_POST['pass']))
        $pass = $_POST['pass'];
    if(isset($_POST['email']))
        $email = $_POST['email'];
    
    $userObj = new User();

    if(!empty($user) && !empty($pass) && !empty($email)){
        $json = $userObj->createUser($user,$pass,$email);

        echo json_encode($json);
    }

    if(!empty($user) && !empty($pass) && empty($email)){
        $json = $userObj->loginUser($user, $pass);

        echo json_encode($json);
    }
?>