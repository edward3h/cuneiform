@import java.util.Map

@param Map<String, String> factionLinks

<html>
<head><title>Cuniform - Datasheet Helper</title>
    <link rel="stylesheet" href="assets/style.css">
</head>
<body class="bg-base-200 pt-4">
<div class="header mx-4">
<h1 class="text-xl font-bold">Factions</h1>
</div>
@for(var entry: factionLinks.entrySet())
    <a href="${entry.getValue()}" class="block text-lg m-4 p-2 bg-base-100 rounded btn btn-outline btn-primary">${entry.getKey()}</a>
@endfor
</body>
</html>