const fs = require('fs');
const path = require('path');

const ROOT = path.join(__dirname);
const PROJECT_DIRS = [
    'ruleengine',
    'ruleengine-client-app',
    'rcm',
    'rcm-client-app',
    'eagleSoftServer',
    'es_data_replication',
];

const SKIP_DIRS = new Set([
    'node_modules', 'target', 'dist', 'build', '.git', '.angular', 'out',
    'coverage', '.idea', '.vscode', 'e2e', 'tmp', '.cache', 'vendor',
]);

const EXTENSIONS = new Set([
    '.java', '.ts', '.tsx', '.js', '.jsx', '.html', '.css', '.scss', '.less',
    '.xml', '.yml', '.yaml', '.json', '.properties', '.md', '.sh', '.sql',
]);

function countLinesInFile(filePath) {
    try {
        const content = fs.readFileSync(filePath, 'utf8');
        return content.split(/\r?\n/).length;
    } catch {
        return 0;
    }
}

function countLinesInDir(dirPath, baseLen) {
    let total = 0;
    let entries;
    try {
        entries = fs.readdirSync(dirPath, { withFileTypes: true });
    } catch {
        return 0;
    }
    for (const e of entries) {
        const full = path.join(dirPath, e.name);
        if (e.isDirectory()) {
            if (SKIP_DIRS.has(e.name)) continue;
            total += countLinesInDir(full, baseLen);
        } else {
            const ext = path.extname(e.name).toLowerCase();
            if (EXTENSIONS.has(ext) || EXTENSIONS.size === 0) {
                total += countLinesInFile(full);
            }
        }
    }
    return total;
}

function main() {
    const results = [];
    let grandTotal = 0;

    for (const dir of PROJECT_DIRS) {
        const fullPath = path.join(ROOT, dir);
        if (!fs.existsSync(fullPath) || !fs.statSync(fullPath).isDirectory()) {
            results.push({ name: dir, lines: 0, exists: false });
            continue;
        }
        const lines = countLinesInDir(fullPath);
        results.push({ name: dir, lines, exists: true });
        grandTotal += lines;
    }

    console.log('\nLines per project (source files under rule_engine):\n');
    console.log('Project                    | Lines');
    console.log('---------------------------|--------');
    for (const r of results) {
        const label = r.exists ? r.name : `${r.name} (missing)`;
        console.log(`${label.padEnd(26)} | ${r.lines.toLocaleString()}`);
    }
    console.log('---------------------------|--------');
    console.log(`${'TOTAL'.padEnd(26)} | ${grandTotal.toLocaleString()}\n`);
}

main();