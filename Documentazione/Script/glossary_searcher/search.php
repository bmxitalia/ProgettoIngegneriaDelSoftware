<?php
/**
 * Search first occurrancy of glossary items in document
 * 
 * Usage:
 *      php search.php /path/to/document.tex
 */

require __DIR__ . "/inc/display.php";
require __DIR__ . "/inc/document.php";
require __DIR__ . "/inc/glossary.php";

if (count($argv) < 2) {
    echo "Usage: php search.php /path/to/document.tex\n";
    exit(0);
}

$document = realpath($argv[1]);
$glossary = realpath(__DIR__ . "/../../LaTeX/Documenti/Esterni/Glossario/sections");

if (empty($document) || !file_exists($document)) {
    echo "Document not found.\n";
    exit(1);
}

if (empty($glossary) || !file_exists($glossary) || !is_dir($glossary)) {
    echo "Glossary not found.\n";
    exit(1);
}

$words = loadGlossary($glossary);

echo count($words) . " words found in glossary.\n";

$content = file_get_contents($document);

$occurrences = findOccurrences($content, $words);

echo "Occurrances:\n";
foreach ($occurrences as $line => $result) {
    echo "\e[32m{$line}\e[0m: \t" . markOccurrance($result['words'], $result['text']) . "\n";
}
