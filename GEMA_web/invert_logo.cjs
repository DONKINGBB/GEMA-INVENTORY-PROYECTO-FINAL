const fs = require('fs');
const path = './src/assets/ic_logo_cuadrado_bb.png';
if (fs.existsSync(path)) {
    const b = fs.readFileSync(path).toString('base64');
    fs.writeFileSync('./public/gema_white.svg', `<svg xmlns="http://www.w3.org/2000/svg" width="256" height="256"><filter id="inv"><feComponentTransfer><feFuncR type="table" tableValues="1 0"/><feFuncG type="table" tableValues="1 0"/><feFuncB type="table" tableValues="1 0"/></feComponentTransfer></filter><image href="data:image/png;base64,${b}" width="100%" height="100%" filter="url(#inv)" /></svg>`);
    console.log('success');
} else {
    console.log('file not found');
}
