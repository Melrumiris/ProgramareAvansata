<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Repository Report</title>
    <style>
        body { font-family: 'Segoe UI', Tahoma, sans-serif; background-color: #121212; color: #e0e0e0; margin: 40px; }
        h2 { color: #bb86fc; border-bottom: 2px solid #333; padding-bottom: 10px; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; box-shadow: 0 4px 8px rgba(0,0,0,0.5); }
        th, td { border: 1px solid #333; padding: 12px; text-align: left; }
        th { background-color: #1f1b24; color: #bb86fc; }
        tr:nth-child(even) { background-color: #1e1e1e; }
        a { color: #03dac6; text-decoration: none; font-weight: bold; }
        a:hover { text-decoration: underline; color: #70efde; }
    </style>
</head>
<body>
<h2>Catalog Report</h2>
<table>
    <tr>
        <th>ID</th>
        <th>Title</th>
        <th>Authors</th>
        <th>Year</th>
        <th>Location</th>
        <th>Concepts</th>
    </tr>
    <#list resources as res>
        <tr>
            <td>${res.id()}</td>
            <td>${res.title()}</td>
            <td>${res.authors()}</td>
            <td>${res.year()}</td>
            <td><a href="${res.location()}" target="_blank">Access</a></td>
            <td>${res.concepts()?join(", ")}</td>
        </tr>
    </#list>
</table>
</body>
</html>