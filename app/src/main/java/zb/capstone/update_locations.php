<?php
function recordLocation()
{
    $response = array();
    $userId = $_POST["userid"];
    $userName = $_POST["username"];
    $userLat = $_POST["lat"];
    $userLng = $_POST["lng"];

    //write to text document? user mysql db?

    // echo json response
    echo json_encode($response);
}
recordLocation();
?>