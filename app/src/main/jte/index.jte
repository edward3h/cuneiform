@import java.util.Map

@param Map<String, String> factionLinks

<html>
<head><title>Cuniform - Datasheet Helper</title>
    <link rel="stylesheet" href="assets/style.css">
</head>
<body class="bg-base-200 pt-4">
<div class="header mx-4">
<h1 class="text-xl font-bold px-4 mx-auto w-max">Factions</h1>
</div>
<div class="columns-3xs gap-4 p-4 m-auto max-w-6xl">
@for(var entry: factionLinks.entrySet())
    <a href="${entry.getValue()}" class="text-lg h-fit w-full mt-4 p-2 bg-base-100 rounded btn btn-outline btn-primary">${entry.getKey()}</a>
@endfor
</div>
</body>
</html>