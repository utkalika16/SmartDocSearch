# 5-Minute PBL Demo Script

## 0:00–0:30 — Problem
"Users remember document content, not filenames. Exact search misses spelling variations and repeated brute-force scanning is inefficient."

## 0:30–1:00 — Architecture
Show the flow:
Document -> normalize -> inverted index + suffix array
Query -> KMP + Rabin-Karp + edit distance + suffix similarity -> ranking

## 1:00–2:00 — Exact search
Search `machine learning`.
Point to:
- term coverage
- KMP exact phrase count
- Rabin-Karp count
- score and preview

## 2:00–3:00 — Fuzzy search
Search `machine lerning`.
Explain that Levenshtein DP measures edit distance and allows approximate retrieval.

## 3:00–4:00 — DSA proof
Open:
- KMP.java
- RabinKarp.java
- EditDistance.java
- SuffixArray.java
Explain each complexity.

## 4:00–5:00 — Evaluation and future scope
Mention:
- current runnable corpus is TXT
- PDF/DOCX/OCR are planned ingestion adapters
- future Aho-Corasick, multilingual support, benchmarks and web UI
