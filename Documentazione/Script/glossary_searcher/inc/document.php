<?php

/**
 * Find occurrences of words in content
 * 
 * @param string $content
 * @param array $words
 * @param boolean $onlyFirst
 * @return array
 */
function findOccurrences($content, $words, $onlyFirst = true) {
    $results = [];

    $lines = explode("\n", $content);

    for ($i = 0;$i < count($lines);$i++) {
        $found = wordsInLine($lines[$i], $words);
        
        if (count($found) > 0) {
            $results[$i + 1] = [
                "words" => $found,
                "text" => $lines[$i],
            ];

            if ($onlyFirst) {
                $words = unsetWords($words, $found);
            }
        }
    }

    return $results;
}

/**
 * Find occurrences of words in line
 * 
 * @param string $line
 * @param array $words
 * @return array
 */
function wordsInLine($line, $words) {
    $found = [];

    foreach ($words as $word) {
        if (strpos($line, $word) !== false) {
            $found[] = $word;
        }
    }

    return $found;
}

/**
 * Remove words found from total
 * 
 * @param array $words
 * @param array $toRemove
 * @return array
 */
function unsetWords($words, $toRemove) {
    foreach ($toRemove as $word) {
        $index = array_search($word, $words);

        if ($words !== false) {
            unset($words[$index]);
        }
    }

    return array_values($words);
}
