<?php
function recordLocation()
{
    //$userId = $_GET["userid"];
    //$userName = $_GET["username"];
    $userLat = $_GET["lat"];
    $userLng = $_GET["lng"];

    //write to text document? use mysql db?
    $file = fopen("testwrite.txt", "w") or die("Unable to open file!");
    $txt = "Lat: " + $userLat + " | Lng: " + $userLng + "\n";
    fwrite($file, $txt);
    fclose($file);

}
    recordLocation();
?>