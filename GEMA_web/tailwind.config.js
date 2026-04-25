/** @type {import('tailwindcss').Config} */
export default {
    content: [
        "./index.html",
        "./src/**/*.{js,ts,jsx,tsx}",
    ],
    darkMode: 'class',
    theme: {
        extend: {
            colors: {
                primary: "#0d2558",
                "primary-dark": "#081A3F",
                accent: "#1e3a8a",
                "light-gray-bg": "#f0f2f5",
                "yellow-light-bg": "#fefce8",
                success: "#16a34a",
                danger: "#dc2626",
            },
            fontFamily: {
                sans: ['Inter', 'system-ui', 'sans-serif'],
            }
        },
    },
    plugins: [],
}
