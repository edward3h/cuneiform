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
    <div><a href="../" class="link link-hover">Index</a></div>
    <h1 class="text-xl font-bold mx-4">${faction.name()}</h1>
</div>
<div id="options" class="grid grid-cols-3 gap-4 justify-center m-4">
    @template.toggle(key = "general", display = "Army Rules")
    @template.toggle(key = "armoury", display = "Armoury")
    @template.toggle(key = "datasheet-back", display = "Hide Datasheet Backs", exclude = true)
    <fieldset class="col-span-3 grid grid-cols-4 gap-4 justify-center"><legend class="font-bold text-lg">Units</legend>
        @for(var datasheet: faction.datasheets())
            @template.toggle(key = "unit-" + Util.safeName(datasheet.title()), display = datasheet.title(), boxKeywords = datasheet.keywords())
        @endfor
        <div class="col-start-1"><button id="allUnits" class="btn btn-primary btn-outline">Select All</button></div>
        <div class="col-start-2"><button id="noUnits" class="btn btn-secondary btn-outline">Select None</button></div>
    </fieldset>
</div>
<div id="pages" class="m-4">
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
