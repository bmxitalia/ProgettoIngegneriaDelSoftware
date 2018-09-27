<?php

// Usage: php generate_summary.php

$mapping = json_decode(file_get_contents('./tracing.json'));
$counts = [];

// Calculate counts
foreach ($mapping as $req => $sources) {
    $type = substr($req, 0, 2);
    $severity = substr($req, 2, 1);
    
    if (!array_key_exists($type, $counts)) {
        $counts[$type] = [];
    }
    

    if (array_key_exists($severity, $counts[$type])) {
        $counts[$type][$severity]++;
    }else{
        $counts[$type][$severity] = 1;
    }

}

// Create file
ob_start();
?>
\subsection{Riepilogo requisiti} \label{riepilogo requisiti}

\begin{center}
    \begin{longtable}{ | >{\centering\arraybackslash}m{2.5cm} | >{\centering\arraybackslash}m{2.5cm} | >{\centering\arraybackslash}m{2.5cm} | >{\centering\arraybackslash}m{2.5cm} | }
        
        \hline
        \textbf{Tipo} & \textbf{Obbligatorio} & \textbf{Desiderabile} & \textbf{Facoltativo}  \\ \hline
        \endhead

        Funzionale & <?= $counts['RF']['O'] ?? 0 ?> & <?= $counts['RF']['D'] ?? 0 ?> & <?= $counts['RF']['F'] ?? 0 ?>  \\ \hline
        Prestazionale & <?= $counts['RP']['O'] ?? 0 ?> & <?= $counts['RP']['D'] ?? 0 ?> & <?= $counts['RP']['F'] ?? 0 ?>  \\ \hline
        Di Qualità & <?= $counts['RQ']['O'] ?? 0 ?> & <?= $counts['RQ']['D'] ?? 0 ?> & <?= $counts['RQ']['F'] ?? 0 ?>  \\ \hline
        Di Vincolo & <?= $counts['RV']['O'] ?? 0 ?> & <?= $counts['RV']['D'] ?? 0 ?> & <?= $counts['RV']['F'] ?? 0 ?>  \\ \hline

       	\caption[Riepilogo requisiti]{Tabella di riepilogo requisiti}
	\end{longtable}
	
\end{center}
<?php

$content = ob_get_clean();

// Save file
file_put_contents('../riepilogo.tex', $content);

echo "DONE\n";
