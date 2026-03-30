<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Movies Report</title>
    <style>
        body { font-family: 'Segoe UI', Tahoma, sans-serif; background-color: #0f172a; color: #e2e8f0; margin: 32px; }
        h1 { color: #38bdf8; border-bottom: 2px solid #1e293b; padding-bottom: 10px; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; box-shadow: 0 4px 8px rgba(0,0,0,0.35); }
        th, td { border: 1px solid #1e293b; padding: 10px 12px; text-align: left; }
        th { background-color: #0ea5e9; color: #0f172a; }
        tr:nth-child(even) { background-color: #111827; }
        tr:nth-child(odd) { background-color: #0b1220; }
        .muted { color: #94a3b8; font-size: 0.9em; }
    </style>
</head>
<body>
<h1>Movies Report</h1>
<table>
    <tr>
        <th>ID</th>
        <th>Title</th>
        <th>Genre</th>
        <th>Release Date</th>
        <th>Duration</th>
        <th>Score</th>
    </tr>
    <#if movies?has_content>
        <#list movies as movie>
            <tr>
                <td>${movie.id}</td>
                <td>${movie.title}</td>
                <td>${movie.genre.name}</td>
                <td>${movie.releaseDate?string('yyyy-MM-dd')!'-'}</td>
                <td>${movie.duration?string('HH:mm:ss')!'-'}</td>
                <td>${movie.score?c}</td>
            </tr>
        </#list>
    <#else>
        <tr>
            <td colspan="6" class="muted">No movies found in the database.</td>
        </tr>
    </#if>
</table>
</body>
</html>

