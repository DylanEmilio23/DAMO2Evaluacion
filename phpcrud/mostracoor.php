<?php


if ($_SERVER["REQUEST_METHOD"] == "GET") {
    require_once 'conexion.php';

    $sql = "SELECT * FROM coordinador";
    $resultado = $mysql->query($sql);

    $datos = array();
    while ($row = $resultado->fetch_assoc()) {
        $datos[] = $row;
    }

    header('Content-Type: application/json');
    echo json_encode($datos);

    $resultado->close();
}





?>