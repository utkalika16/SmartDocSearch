
# Smart Document Search System — DSA PBL

## 1. Project idea

Smart Document Search is a Java-based document retrieval engine that finds relevant text documents without depending only on exact filenames. It demonstrates advanced DSA topics from the semester:

- KMP string matching
- Rabin-Karp with double hashing
- Levenshtein edit distance
- Suffix array + Kasai LCP
- Custom hash table / inverted index
- Custom dynamic arrays and linked lists
- Relevance scoring and ranking
- File I/O and a Swing user interface

The Review-1 plan lists Java, File I/O, Custom Data Structures, and KMP, Rabin-Karp, Edit Distance and Suffix Array as the main technologies/algorithms. The implementation below turns those planned modules into a working prototype.

## 2. Important implementation scope

The runnable version directly supports `.txt` documents. This keeps the DSA engine dependency-free and makes every core algorithm visible in the source code.

The literature/research-gap presentation proposes PDF, DOCX and OCR as a broader system direction. Those adapters are not silently claimed as implemented here; they can be added as a separate ingestion layer later.

## 3. Architecture

Documents
  -> File Loader
  -> Text Normalizer
  -> Custom Inverted Index
  -> Suffix Array + LCP per document

User Query
  -> Normalize + tokenize
  -> Inverted-index candidate signals
  -> KMP exact phrase search
  -> Rabin-Karp double-hash verification
  -> Levenshtein fuzzy similarity
  -> Suffix-array prefix similarity
  -> Weighted relevance score
  -> Insertion-sort ranking
  -> Explainable result + preview

## 4. Why each algorithm is used

### Custom hash table / inverted index
Maps a normalized word to document IDs. This avoids scanning every document for every individual query term.

### KMP
Finds an exact query phrase in linear time after failure-function preprocessing.

### Rabin-Karp
Uses rolling hashes to locate candidate phrase matches. Two modular hashes reduce collision risk, followed by character verification.

### Levenshtein distance
Measures how many insertions, deletions and substitutions are needed to transform the query into a local document text. This handles misspellings and approximate queries.

### Suffix array + LCP
Stores sorted suffixes of each document and uses LCP-style prefix comparison to measure how much of the query matches a suffix. This connects the similarity requirement to suffix structures.

### Ranking
The final score combines:
- term coverage: 45
- exact phrase signal: 25
- suffix-prefix similarity: 15
- fuzzy term similarity: 15
- additional exact-match boost: 10

Fuzzy similarity is computed term-by-term: each query term is compared with the closest document token using Levenshtein similarity. This makes spelling variations more meaningful than comparing the whole query with a long document chunk.

The score is a project-specific heuristic, not a universal information-retrieval metric.

## 5. Complexity

Let n be document text length, m be query length, and k be number of query terms.

- Normalization/tokenization: O(n)
- Inverted-index lookup: expected O(k)
- KMP: O(n + m)
- Rabin-Karp: expected O(n + m), with verification
- Levenshtein: O(mn) in the local comparison window
- Suffix array construction: practical O(n log^2 n) in this implementation
- Kasai LCP: O(n)
- Ranking: O(r^2) using insertion sort for r returned results

The suffix-array implementation is intentionally practical and readable rather than SA-IS.

## 6. Run

Requirements: JDK 17 or later.

From the project root:

```bash
javac -d out src/*.java
java -cp out Main
```

On Windows PowerShell the same commands work.

The program loads all `.txt` files from `docs/`.

## 7. Demo queries

Try:

1. `machine learning`
2. `neural networks`
3. `graph algorithms`
4. `database indexing`
5. `datastructures`
6. `machine lerning`  (misspelling)
7. `traffic signal`
8. `renewable energy`

The result shows the document, score, term coverage, KMP count, Rabin-Karp count, edit distance, suffix-prefix value, and a matching preview.

## 8. DSA mapping for viva

| Requirement | Project implementation |
|---|---|
| String matching | KMP + Rabin-Karp |
| Dynamic programming | Levenshtein edit distance |
| Suffix structures | Suffix array + LCP |
| Indexing | Custom hash table / inverted index |
| Ranking | Custom insertion sort |
| Data structures | Dynamic arrays, linked lists, postings |
| File processing | Java File I/O |
| Explainability | Match signals + preview |

## 9. Suggested future extensions

- PDF/DOCX extraction adapters
- OCR for scanned documents
- Aho-Corasick for multi-pattern queries
- BM25-style scoring
- compressed postings lists
- cross-document suffix array
- multilingual tokenization
- web UI
- performance benchmark dashboard

These are extensions, not claims about the current implementation.
