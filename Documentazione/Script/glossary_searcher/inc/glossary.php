<?php

/**
 * Load glossary words from sections directory
 * 
 * @param string $path
 * @return array
 */
function loadGlossary($path) {
    $words = [];
    $sections = scandir($path);
    
    foreach ($sections as $name) {
        $file = realpath($path . DIRECTORY_SEPARATOR . $name);
        if (is_file($file)) {
            $words = array_merge($words, loadGlossaryFile($file));
        }
    }
    
    return $words;
}

/**
 * Load glossary words from file
 * 
 * @param string $path
 * @return array
 */
function loadGlossaryFile($path) {
    $words = [];
    
    $content = file_get_contents($path);

    $count = preg_match_all("|\\\\subsection\\*\\{([^\\}]+)\\}|", $content, $matches);

    if ($count > 0) {
        foreach ($matches[1] as $word) {
            $words[] = $word;
        }
    }
    
    return $words;
}
