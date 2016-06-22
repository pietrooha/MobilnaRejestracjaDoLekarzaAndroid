<?php

/*
 * Przedstawiony kod ma dodawać nowy termin wizyty do bazy danych
 * Wszystkie szczegóły wizyty są czytane z HTTP Post Request
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_POST['dzien']) && isset($_POST['godzina']) && isset($_POST['imie']) && isset($_POST['nazwisko']) && isset($_POST['pesel'])) {
    
    $dzien = $_POST['dzien'];
    $godzina = $_POST['godzina'];
    $imie = $_POST['imie'];
    $nazwisko = $_POST['nazwisko'];
    $pesel = $_POST['pesel'];
    $nrTel = $_POST['nrTel'];

    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();

    // mysql inserting a new row
    $result = mysql_query("INSERT INTO zarejestrowani(dzien, godzina, imie, nazwisko, pesel, nrTel) VALUES('$dzien', '$godzina', '$imie', '$nazwisko', '$pesel', '$nrTel')");

    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Utworzono nowy termin.";

        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! Wystapił błąd.";
        
        // echoing JSON response
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    // echoing JSON response
    echo json_encode($response);
}
?>