@import com.ordoacerbus.cuneiform.*

@param Faction faction

<html>
<head>
    <title>${faction.name()}</title>
    <link rel="stylesheet" href="../assets/style.css">
    <script src="../assets/client.js"></script>
</head>
<body onload="main()" class="bg-base-200 pt-4 print:pt-0">
<div class="header mx-4">
    <div><a href="../" class="link">Factions Index</a></div>
    <h1 class="text-xl font-bold px-4 mx-auto w-max">${faction.name()}</h1>
</div>
<div id="options" class="p-4 m-auto max-w-6xl">
    <div class="columns-2xs gap-4">
    @template.toggle(key = "general", display = "Army Rules")
        @if (faction.pages().stream().anyMatch(p -> p.type() == PageType.Armoury))
            @template.toggle(key = "armoury", display = "Armoury")
        @endif
    @template.toggle(key = "datasheet-back", display = "Hide Datasheet Backs", exclude = true)
    </div>
    <fieldset><legend class="font-bold text-lg">Units</legend>
        <div class="columns-3xs gap-4">
        @for(var datasheet: faction.datasheets())
            @template.toggle(key = "unit-" + Util.safeName(datasheet.title()), display = datasheet.title(), boxKeywords = datasheet.keywords())
        @endfor
        </div>
        <div class="flex justify-center gap-4 mt-4">
            <button id="allUnits" class="btn btn-primary btn-outline w-40">Select All</button>
            <button id="noUnits" class="btn btn-secondary btn-outline w-40">Select None</button>
        </div>
    </fieldset>
</div>
<div id="pages" class="p-4 m-auto max-w-6xl">
@for(var page: faction.pages())
    @if(page.type() == PageType.General && page.title() != null)
        <div class="page general hidden pb-4 text-center">
            <img alt="${page.title()}" class="max-w-full max-h-[calc(100vh-4rem)]" src="${page.imagePath()}">
        </div>
    @endif
@endfor
@for(var page: faction.pages())
    @if(page.type() == PageType.Armoury)
        <div class="page armoury hidden pb-4 text-center">
            <img alt="${page.title()}" class="max-w-full max-h-[calc(100vh-4rem)]" src="${page.imagePath()}">
        </div>
    @endif
@endfor
@for(var datasheet: faction.datasheets())
    <div class="page datasheet hidden pb-4 text-center rounded datasheet-front ${Util.keywordsClasses(datasheet.keywords())} unit-${Util.safeName(datasheet.title())}">
        <img alt="${datasheet.title()}" class="rounded max-w-full max-h-[calc(100vh-4rem)]" src="${datasheet.pageFront().imagePath()}">
    </div>
    <div class="page datasheet hidden pb-4 rounded datasheet-back ${Util.keywordsClasses(datasheet.keywords())} unit-${Util.safeName(datasheet.title())}">
        <img alt="${datasheet.title()} Options" class="rounded max-w-full max-h-[calc(100vh-4rem)]" src="${datasheet.pageBack().imagePath()}">
    </div>
@endfor
</div>
</body>
</html>
