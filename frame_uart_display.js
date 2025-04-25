// Frame: display any text received over Nordic UART
NRF.setServices(undefined, { uart:true });

function render(text) {
  g.clear();
  g.setFont("Vector",14);
  g.setFontAlign(0,0);
  g.drawString(text, 120, 90);  // center on 240×180 display
  g.flip();
}

render("Waiting…");
NRF.on('data', d => render(d.toString().slice(0,22))); // 22 chars max
