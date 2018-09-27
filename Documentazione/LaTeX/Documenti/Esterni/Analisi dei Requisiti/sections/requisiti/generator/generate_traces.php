<?php

// Usage: php generate_traces.php

// Replace Regex:
//      RF([\w\.]+) & ([^&]+)&(\s+)(\w+)(\s+)UC([\d\.]+)(.+)
//      "RF$1": ["$4", "UC$6"],

$mapping = json_decode(file_get_contents('./tracing.json'));
$base = file_get_contents('./base_file.tex');

/****** Generate tracciamento_requisiti_fonti.tex ******/

echo "Generating tracciamento_requisiti_fonti.tex ... ";

// Generate file
$content = '';

foreach ($mapping as $req => $sources) {
    $content .= "        {$req} & {$sources[0]}\\\\\n";
    
    if (count($sources) > 1) {
        for ($i = 1; $i < count($sources); $i++) {
            $content .= "            & {$sources[$i]} \\\\\n";
        }
    }

    $content .= "\hline\n\n";
}

$file = str_replace(
    ['TITLE', 'LABEL', 'HEADER', 'CONTENT', 'CAPTION'],
    [
        'Tracciamento requisiti-fonti',
        '',
        '\textbf{Id Requisito} & \textbf{Fonti}',
        $content,
        'Tabella di tracciamento requisiti-fonti',
    ],
    $base
);

// Save file
file_put_contents('../tracciamento_requisiti_fonti.tex', $file);

echo "DONE \n";

/****** Generate tracciamento_fonti_requisiti.tex ******/

echo "Generating tracciamento_requisiti_fonti.tex ... ";

// Reverse mapping
$reverseMapping = [];

foreach ($mapping as $req => $sources) {
    foreach ($sources as $source) {
        $reverseMapping[$source][] = $req;
    }
}

ksort($reverseMapping);

// Generate file
$content = '';

foreach ($reverseMapping as $source => $reqs) {
    $content .= "        {$source} & {$reqs[0]}\\\\\n";
    
    if (count($reqs) > 1) {
        for ($i = 1; $i < count($reqs); $i++) {
            $content .= "            & {$reqs[$i]} \\\\\n";
        }
    }

    $content .= "\hline\n\n";
}

$file = str_replace(
    ['TITLE', 'LABEL', 'HEADER', 'CONTENT', 'CAPTION'],
    [
        'Tracciamento fonti-requisiti',
        '\label{tab tracciamento}',
        '\textbf{Fonte} & \textbf{Id Requisiti}',
        $content,
        'Tabella di tracciamento fonti-requisiti',
    ],
    $base
);

// Save file
file_put_contents('../tracciamento_fonti_requisiti.tex', $file);

echo "DONE \n";
