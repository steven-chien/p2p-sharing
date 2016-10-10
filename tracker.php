<?php

// https://caprioli.se/tracker.php?port=169&info_hash=01234567890123456789012345678912&peer_id=12234567890323456789012345678912&key=01234567890123456789012345678912

$info_hash = valdata("info_hash");
$peer_id = valdata("peer_id");
$key = valdata("key");
$port = (int) valdata("port", false);
if ($port < 1 || $port > 65535) die("Port error: $port,".valdata("port", false));

if (!file_exists("/dev/shm/p2p")) {
    file_put_contents("/dev/shm/p2p", serialize(array()));
}

$d = unserialize(file_get_contents("/dev/shm/p2p"));

$d[$peer_id] = array(
    'ip' => $_SERVER['REMOTE_ADDR'],
    'port' => $port,
    'key' => $key,
    'info_hash' => $info_hash,
);
file_put_contents("/dev/shm/p2p", serialize($d));

$no = 0;
$ret = "{peers:[";
$first = true;
foreach($d as $__peer_id => $data) {
    if ($__peer_id == $peer_id) continue;
    if ($no == 10) break;
    if ($data['info_hash'] == $info_hash) {
        if (!$first) $ret .= ",";
        $ret .= "{ip:\"".$data['ip']."\", port:".$data['port']."}";
        
        $first = false;
    }
}

header("Content-type: application/json");
die($ret."]}");

function valdata($g, $fixed_size=true) {
	if (!isset($_GET[$g])) {
		die('Invalid request, missing data');
	}
    
	if (!is_string($_GET[$g])) {
		die('Invalid request, unknown data type');
	}
    
	if ($fixed_size && strlen($_GET[$g]) != 32) {
		die('Invalid request, length on fixed argument not correct');
	}
    
	if (strlen($_GET[$g]) > 80) { //128 chars should really be enough
		die('Request too long');
	}
    return $_GET[$g];
}

?>
