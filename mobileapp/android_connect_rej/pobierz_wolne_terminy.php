<?php

// array for JSON response
$response = array();


// include db connect class
require_once __DIR__ . '/db_connect.php';

// polaczenie z baza danych
$db = new DB_CONNECT();

// pobierz wszystkie wolne terminy z tabeli zarejestrowani
$result = mysql_query("SELECT DISTINCT dzien FROM zarejestrowani ORDER BY dzien") or die(mysql_error());

// check for empty result
if (mysql_num_rows($result) > 0) {
    $response["zarejestrowani"] = array();
    
    while ($row = mysql_fetch_array($result)) {
        // temp user array
        $zarejestrowany = array();
        $zarejestrowany["pid"] = $row["pid"];
        $zarejestrowany["dzien"] = $row["dzien"];
        $zarejestrowany["godzina"] = $row["godzina"];
        $zarejestrowany["imie"] = $row["imie"];
        $zarejestrowany["nazwisko"] = $row["nazwisko"];
        $zarejestrowany["pesel"] = $row["pesel"];
        $zarejestrowany["nrTel"]=$row["nrTel"];
        $zarejestrowany["wolnyTermin"] = $row["wolnyTermin"];



        // push single zarejestrowany into final response array
        array_push($response["zarejestrowani"], $zarejestrowany);
    }
    // success
    $response["success"] = 1;

    // echoing JSON response
    echo json_encode($response);
} else {
    // nie znaleziono wolnych terminów
    $response["success"] = 0;
    $response["message"] = "Nie znaleziono wolnych terminów.";

    // echo no users JSON
    echo json_encode($response);
}
?>