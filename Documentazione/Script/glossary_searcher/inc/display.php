<?php

/**
 * Highligh word occrances inside text
 * 
 * @param array $words
 * @param string $text
 * @return string
 */
function markOccurrance($words, $text) {
    foreach ($words as $word) {
        $text = str_replace($word, "\e[44m{$word}\e[0m", $text);
    }

    return trim($text);
}
