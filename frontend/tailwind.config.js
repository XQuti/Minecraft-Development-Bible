/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#f0f9ff',
          100: '#e0f2fe',
          200: '#bae6fd',
          300: '#7dd3fc',
          400: '#38bdf8',
          500: '#0ea5e9',
          600: '#0284c7',
          700: '#0369a1',
          800: '#075985',
          900: '#0c4a6e',
        },
        secondary: {
          50: '#f8fafc',
          100: '#f1f5f9',
          200: '#e2e8f0',
          300: '#cbd5e1',
          400: '#94a3b8',
          500: '#64748b',
          600: '#475569',
          700: '#334155',
          800: '#1e293b',
          900: '#0f172a',
        },
        minecraft: {
          green: '#00ff00',
          blue: '#5555ff',
          red: '#ff5555',
          yellow: '#ffff55',
          purple: '#ff55ff',
          aqua: '#55ffff',
          white: '#ffffff',
          gray: '#aaaaaa',
          dark_gray: '#555555',
          black: '#000000',
        }
      },
      fontFamily: {
        'minecraft': ['Minecraft', 'monospace'],
      }
    },
  },
  plugins: [],
}