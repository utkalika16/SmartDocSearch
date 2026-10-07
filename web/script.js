const $=id=>document.getElementById(id);
async function updateStats(){try{const d=await (await fetch('/stats')).json();if($('docCount'))$('docCount').textContent=d.documents;}catch(e){}}
async function searchDocs(){const q=$('query').value.trim();if(!q){$('results').innerHTML='<p class="hint">Enter a search query.</p>';return}$('results').innerHTML='<p class="hint">Searching...</p>';try{const method=$('method').value;const d=await (await fetch('/search?q='+encodeURIComponent(q)+'&method='+encodeURIComponent(method))).json();let h='<div class="hint"><b>'+d.method+'</b> mode • '+d.results.length+' result(s)</div>';if(!d.results.length)h+='<div class="notice">No result for this method. Try Automatic or Edit Distance for a misspelled query.</div>';d.results.forEach(x=>h+='<div class="result"><div class="resultTop"><b>📄 '+x.document+'</b><span class="score">'+x.score.toFixed(2)+'</span></div><div class="reason">'+x.reason+'</div><div class="preview">'+x.preview+'</div></div>');$('results').innerHTML=h}catch(e){$('results').innerHTML='<p class="hint">Backend connection failed. Start WebServer first.</p>'}}
function renderCompareChart(benchmarks){
const chart=$('compareChart');
if(!chart)return;
if(!benchmarks||!benchmarks.length){chart.innerHTML='<div class="hint">No benchmark data available.</div>';return}
const rows=benchmarks.map(x=>({name:String(x.algorithm),time:Number(x.timeNs)||0,performance:Number(x.performance)||0}));
const maxTime=Math.max(...rows.map(x=>x.time),1);
const width=980,height=Math.max(390,rows.length*55+95),left=205,right=110,top=55,bottom=55;
const plotW=width-left-right,plotH=height-top-bottom,gap=plotH/rows.length,barH=Math.min(34,gap*.55);
const ticks=5;
let svg='<svg class="performanceSvg" viewBox="0 0 '+width+' '+height+'" role="img" aria-label="Average execution time comparison chart">';
svg+='<text x="'+(width/2)+'" y="28" text-anchor="middle" class="chartTitle">Average Execution Time (ns)</text>';
for(let i=0;i<=ticks;i++){
 const x=left+(plotW*i/ticks),val=maxTime*i/ticks;
 svg+='<line x1="'+x.toFixed(1)+'" y1="'+top+'" x2="'+x.toFixed(1)+'" y2="'+(height-bottom)+'" class="chartGrid"/>';
 svg+='<text x="'+x.toFixed(1)+'" y="'+(height-bottom+25)+'" text-anchor="middle" class="chartAxis">'+Math.round(val).toLocaleString()+'</text>';
}
rows.forEach((r,i)=>{
 const cy=top+gap*i+gap/2,y=cy-barH/2,bw=Math.max(r.time/maxTime*plotW,4);
 svg+='<text x="'+(left-14)+'" y="'+(cy+5)+'" text-anchor="end" class="chartLabelSvg">'+escapeHtml(r.name)+'</text>';
 svg+='<rect x="'+left+'" y="'+y.toFixed(1)+'" width="'+bw.toFixed(1)+'" height="'+barH.toFixed(1)+'" rx="7" class="chartBarSvg"><title>'+escapeHtml(r.name)+': '+r.time.toLocaleString()+' ns</title></rect>';
 svg+='<text x="'+Math.min(left+bw+10,width-right+5)+'" y="'+(cy+5)+'" class="chartValueSvg">'+r.time.toLocaleString()+' ns</text>';
});
svg+='</svg>';
chart.innerHTML=svg;

const score=document.createElement('div');
score.className='scoreChartWrap';
score.innerHTML='<div class="scoreChartTitle">Performance Score</div>';
const maxScore=100;
rows.forEach(r=>{
 const row=document.createElement('div'); row.className='scoreRow';
 const label=document.createElement('div'); label.className='scoreName'; label.textContent=r.name;
 const track=document.createElement('div'); track.className='scoreTrack';
 const fill=document.createElement('div'); fill.className='scoreFill'; fill.style.width=Math.max(0,Math.min(r.performance,maxScore))+'%';
 const value=document.createElement('div'); value.className='scoreValue'; value.textContent=r.performance.toFixed(1);
 track.appendChild(fill); row.append(label,track,value); score.appendChild(row);
});
chart.appendChild(score);
}
function escapeHtml(value){return value.replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));}

async function compare(){const q=$('compareQuery').value.trim();if(!q){$('compareOut').innerHTML='<p class="hint">Enter a query first.</p>';return}$('compareOut').innerHTML='<p class="hint">Benchmarking all algorithms...</p>';try{const d=await (await fetch('/compare?q='+encodeURIComponent(q))).json();let h='<table class="table"><tr><th>Algorithm</th><th>Match / Value</th><th>Avg Time (ns)</th><th>Quality</th><th>Performance</th><th>Overall</th></tr>';d.benchmarks.forEach(x=>{let value=x.algorithm==='Edit Distance'?(x.value*100).toFixed(1)+'% similarity':x.value;h+='<tr><td><b>'+x.algorithm+'</b></td><td>'+value+'</td><td>'+x.timeNs+'</td><td>'+x.quality+'</td><td>'+x.performance+'</td><td>'+x.overall+'</td></tr>'});h+='</table>';renderCompareChart(d.benchmarks);h+='<div class="best"><h3>🏆 Best Algorithm for this Query: '+d.best+'</h3><div>'+d.bestReason+'</div><div class="hint">Recommendation is calculated from this benchmark. '+d.runs+' repeated runs are used for each method.</div></div>';h+='<p class="hint">Use: '+d.benchmarks.map(x=>x.algorithm+' — '+x.use).join(' • ')+'</p>';$('compareOut').innerHTML=h}catch(e){$('compareOut').innerHTML='<p class="hint">Benchmark failed. Make sure WebServer is running.</p>'}}
async function loadGuide(){if(!$('guide'))return;try{const d=await (await fetch('/guide')).json();$('guide').innerHTML=d.map(x=>'<div class="guideItem"><b>'+x.algorithm+'</b> <span class="status '+(x.status.includes('Implemented')?'impl':'ext')+'">'+x.status+'</span><div>'+x.purpose+'</div><div class="complex">Advantage: '+x.advantage+' • Complexity: '+x.complexity+'</div></div>').join('')}catch(e){$('guide').textContent='Could not load guide.'}}
async function addDoc(){const title=$('title').value.trim(),content=$('content').value.trim();if(!title||!content){$('addOut').textContent='Enter title and content.';return}try{const d=await (await fetch('/add',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({title,content})})).json();$('addOut').textContent=d.message+': '+d.document+' • total indexed: '+d.count;$('title').value='';$('content').value='';updateStats()}catch(e){$('addOut').textContent='Could not add document.'}}
async function align(){const a=$('a').value.trim(),b=$('b').value.trim();if(!a||!b){$('alignOut').textContent='Enter both texts.';return}try{const d=await (await fetch('/alignment?a='+encodeURIComponent(a)+'&b='+encodeURIComponent(b))).json();$('alignOut').innerHTML='<span class="metric">Edit Distance: '+d.editDistance+'</span><span class="metric">Needleman-Wunsch: '+d.needlemanWunsch+'</span><span class="metric">Smith-Waterman: '+d.smithWaterman+'</span>'}catch(e){$('alignOut').textContent='DP calculation failed.'}}
updateStats();loadGuide();

// ---------------- Extra features: Dark Mode + Document Analyzer ----------------
function applyDarkMode(on){
  document.body.classList.toggle('darkMode',on);
  const b=$('themeToggle');
  if(b)b.textContent=on?'☀️ Light':'🌙 Dark';
  try{localStorage.setItem('smartsearch-dark',on?'1':'0');}catch(e){}
}
function toggleDarkMode(){applyDarkMode(!document.body.classList.contains('darkMode'));}
function initDarkMode(){
  let on=false;
  try{on=localStorage.getItem('smartsearch-dark')==='1';}catch(e){}
  const nav=document.querySelector('.nav>div:last-child');
  if(nav && !$('themeToggle')){
    const b=document.createElement('button');
    b.id='themeToggle'; b.type='button'; b.className='themeToggle'; b.onclick=toggleDarkMode; b.textContent=on?'☀️ Light':'🌙 Dark'; nav.appendChild(b);
  }
  applyDarkMode(on);
}

let uploadedDocumentText='';
let uploadedDocumentName='';
function analyzeUploadedDocument(){
  const input=$('docFile');
  if(!input||!input.files||!input.files.length){$('docOut').textContent='Choose a TXT document first.';return;}
  const file=input.files[0];
  if(file.type && file.type!=='text/plain' && !file.name.toLowerCase().endsWith('.txt')){$('docOut').textContent='Please upload a TXT document.';return;}
  const reader=new FileReader();
  reader.onload=()=>{
    uploadedDocumentText=String(reader.result||''); uploadedDocumentName=file.name;
    const words=uploadedDocumentText.match(/[A-Za-z0-9]+(?:['’-][A-Za-z0-9]+)*/g)||[];
    const normalized=words.map(w=>w.toLowerCase());
    const unique=new Set(normalized);
    const lines=uploadedDocumentText.length?uploadedDocumentText.split(/\r?\n/).length:0;
    const chars=uploadedDocumentText.length;
    const nonSpace=(uploadedDocumentText.match(/\S/g)||[]).length;
    $('docOut').innerHTML='<b>'+escapeHtml(uploadedDocumentName)+'</b> loaded successfully. This analysis is temporary and does not modify your existing indexed documents.';
    $('docStats').innerHTML='<div class="docStat"><b>'+words.length.toLocaleString()+'</b><span>Words</span></div><div class="docStat"><b>'+unique.size.toLocaleString()+'</b><span>Unique Words</span></div><div class="docStat"><b>'+chars.toLocaleString()+'</b><span>Characters</span></div><div class="docStat"><b>'+nonSpace.toLocaleString()+'</b><span>Non-space Characters</span></div><div class="docStat"><b>'+lines.toLocaleString()+'</b><span>Lines</span></div>';
    $('docStatsSection').classList.remove('hidden');
    renderWordChart(normalized);
    $('docWordsSection').classList.remove('hidden');
    $('docAlgoSection').classList.remove('hidden');
    const first=normalized.find(Boolean)||'';
    $('docQuery').value=first;
    $('algoRecommendation').textContent='Enter a query from this document and run the algorithms. The benchmark will use only this uploaded document.';
    if(first)runDocumentAlgorithms();
  };
  reader.onerror=()=>{$('docOut').textContent='Could not read the selected document.';};
  reader.readAsText(file);
}
function renderWordChart(words){
  const counts={}; words.forEach(w=>{if(w.length>1)counts[w]=(counts[w]||0)+1;});
  const top=Object.entries(counts).sort((a,b)=>b[1]-a[1]).slice(0,8);
  if(!top.length){$('wordChart').innerHTML='<p class="hint">No words found.</p>';return;}
  const max=top[0][1], width=900, left=150, barMax=620, row=44, height=top.length*row+45;
  let svg='<svg class="performanceSvg wordSvg" viewBox="0 0 '+width+' '+height+'" role="img" aria-label="Most frequent words in uploaded document"><line x1="'+left+'" y1="20" x2="'+left+'" y2="'+(height-15)+'" class="chartGrid"/>';
  top.forEach((x,i)=>{const y=30+i*row;const bw=Math.max(4,barMax*x[1]/max);svg+='<text x="'+(left-10)+'" y="'+(y+15)+'" text-anchor="end" class="chartLabelSvg">'+escapeXml(x[0])+'</text><rect x="'+left+'" y="'+y+'" width="'+bw+'" height="24" rx="6" class="chartBarSvg"></rect><text x="'+(left+bw+8)+'" y="'+(y+17)+'" class="chartValueSvg">'+x[1]+'</text>';});
  svg+='</svg>'; $('wordChart').innerHTML=svg;
}
async function runDocumentAlgorithms(){
  if(!uploadedDocumentText){$('algoRecommendation').textContent='Upload a document first.';return;}
  const q=($('docQuery').value||'').trim();
  if(!q){$('algoRecommendation').textContent='Enter a query to benchmark the algorithms on this document.';return;}
  $('algoRecommendation').textContent='Running all implemented algorithms on '+uploadedDocumentName+'...';
  try{
    const response=await fetch('/document-analysis',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({text:uploadedDocumentText,query:q})});
    if(!response.ok)throw new Error('request failed');
    const d=await response.json();
    renderDocumentAlgorithmChart(d.benchmarks||[]);
    let best=d.best||'No suitable match';
    const reason=d.bestReason||'';
    $('algoRecommendation').innerHTML='<b>Recommended for this query: '+escapeHtml(best)+'</b><br>'+escapeHtml(reason)+'<br><span class="hint">The result is measured on this uploaded document only.</span>';
    let h='<table class="table"><thead><tr><th>Algorithm</th><th>Match / Value</th><th>Avg Time (ns)</th><th>Performance</th><th>Overall</th></tr></thead><tbody>';
    (d.benchmarks||[]).forEach(r=>{h+='<tr><td><b>'+escapeHtml(r.algorithm)+'</b></td><td>'+Number(r.value).toFixed(2)+'</td><td>'+Number(r.timeNs).toLocaleString()+'</td><td>'+Number(r.performance).toFixed(1)+'</td><td>'+Number(r.overall).toFixed(1)+'</td></tr>';});
    h+='</tbody></table>'; $('docAlgoTable').innerHTML=h;
  }catch(e){$('algoRecommendation').textContent='Could not run the document benchmark. Make sure WebServer.java is running.';}
}
function renderDocumentAlgorithmChart(rows){
  const el=$('docAlgoChart'); if(!el)return;
  if(!rows.length){el.innerHTML='<p class="hint">No benchmark data available.</p>';return;}
  const sorted=rows.slice().sort((a,b)=>Number(a.timeNs)-Number(b.timeNs));
  const max=Math.max(...sorted.map(r=>Number(r.timeNs)),1), width=980,left=180,barMax=650,row=48,height=sorted.length*row+55;
  let svg='<h3>Execution Time on This Document</h3><svg class="performanceSvg" viewBox="0 0 '+width+' '+height+'" role="img" aria-label="Algorithm execution time for uploaded document">';
  sorted.forEach((r,i)=>{const y=25+i*row;const bw=Math.max(5,barMax*Number(r.timeNs)/max);svg+='<text x="'+(left-12)+'" y="'+(y+18)+'" text-anchor="end" class="chartLabelSvg">'+escapeXml(r.algorithm)+'</text><rect x="'+left+'" y="'+y+'" width="'+bw+'" height="26" rx="6" class="chartBarSvg"></rect><text x="'+(left+bw+8)+'" y="'+(y+18)+'" class="chartValueSvg">'+Number(r.timeNs).toLocaleString()+' ns</text>';});
  svg+='</svg>'; el.innerHTML=svg;
}
function escapeXml(s){return String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;').replace(/'/g,'&apos;');}
function escapeHtml(s){return escapeXml(s);}

if(document.readyState==='loading')document.addEventListener('DOMContentLoaded',initDarkMode);else initDarkMode();
