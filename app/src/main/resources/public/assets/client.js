
let toggles;
let pages;

function hide(el) {
    el.style.display = 'none';
}

function show(el) {
    el.style.display = 'block';

}

function updateDisplay() {
    for (const page of pages) {
        hide(page);
    }

    for (const toggle of toggles) {
        if (toggle.checked && toggle.value == 'include') {
            for (const page of pages) {
                if (page.classList.contains(toggle.name)) {
                    show(page);
                }
            }
        }
    }
    for (const toggle of toggles) {
        if (toggle.checked && toggle.value == 'exclude') {
            for (const page of pages) {
                if (page.classList.contains(toggle.name)) {
                    hide(page);
                }
            }
        }
    }
}

function all(checked) {
    for (const toggle of toggles) {
        if (toggle.name.startsWith("unit-")) {
            toggle.checked = checked;
        }
    }
    updateDisplay();
}

function main() {
    toggles = document.querySelectorAll(".checkbox");
    pages = document.querySelectorAll(".page");

    for (const toggle of toggles) {
        toggle.addEventListener('click', updateDisplay);
    }

    const allUnitsButton = document.getElementById("allUnits");
    allUnitsButton.addEventListener('click', () => all(true));
    const noUnitsButton = document.getElementById("noUnits");
    noUnitsButton.addEventListener('click', () => all(false));

    updateDisplay();
}