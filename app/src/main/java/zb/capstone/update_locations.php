<?php
function recordLocation()
{
    $userId = $_GET["userid"];
    $userLat = $_GET["lat"];
    $userLng = $_GET["lng"];
    $userNcog = $_GET["ncog"];

    //write to text document? use mysql db?
    $file = fopen("testwrite.txt", "w") or die("Unable to open file!");
    $txt = $userId.";".$userLat.";".$userLng.";".$userNcog."\n";
    fwrite($file, $txt);
    fclose($file);

}
    recordLocation();
?>