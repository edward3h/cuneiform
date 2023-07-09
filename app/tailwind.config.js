/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/main/jte/**/*.jte"],
  theme: {
    extend: {},
  },
  plugins: [require("@tailwindcss/typography"), require("daisyui")],
  daisyui: {
    themes: ["fantasy", "night"],
    darkTheme: "night"
  }
}

