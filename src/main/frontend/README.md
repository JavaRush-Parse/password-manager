# Frontend Build Setup

This project uses **esbuild** for JavaScript bundling and **Tailwind CSS** for styling.

## Project Structure

```
src/main/frontend/
├── src/
│   ├── index.js              # Main entry point (imports all modules)
│   ├── modules/              # JavaScript modules
│   │   └── passwordEditForm.js
│   └── tailwind.css          # Tailwind CSS entry
├── package.json
└── tailwind.config.js
```

## Installation

```bash
npm install
```

## Build Commands

### Production Build
```bash
npm run build
```
Builds both CSS and JS (minified) to `src/main/resources/static/`

### Development Watch Mode
```bash
npm run watch
```
Watches for changes and rebuilds automatically

### Individual Commands
- `npm run build:css` - Build CSS only
- `npm run build:js` - Build JS only
- `npm run watch:css` - Watch CSS only
- `npm run watch:js` - Watch JS only

## Adding New JavaScript Modules

1. Create your module in `src/modules/yourModule.js`:
```javascript
export function yourFunction() {
    // your code
}
```

2. Import and expose it in `src/index.js`:
```javascript
import { yourFunction } from './modules/yourModule.js';
window.yourFunction = yourFunction;
```

3. Use it in your HTML templates:
```html
<button onclick="yourFunction()">Click me</button>
```

## Output

- **CSS**: `src/main/resources/static/main.css`
- **JavaScript**: `src/main/resources/static/bundle.js`

Both files are referenced in your Thymeleaf templates.
