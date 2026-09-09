import { useState } from "react";
import "./App.css";

function App() {
  const [jobDescription, setJobDescription] = useState("");
  const [analysis, setAnalysis] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [copiedIndex, setCopiedIndex] = useState(null);

    const handleCopy = async (text, index) => {
        await navigator.clipboard.writeText(text);
        setCopiedIndex(index);

        setTimeout(() => {
            setCopiedIndex(null);
        }, 1500);
    };

    const handleAnalyze = async () => {
        if (!jobDescription.trim()) {
            return;
        }

        setLoading(true);
        setError("");
        setAnalysis(null);

        try {
            const response = await fetch(
                "http://localhost:8080/api/resume/analyze",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        jobDescription: jobDescription,
                    }),
                }
            );

            if (!response.ok) {
                throw new Error("Failed to analyze job description");
            }

            const data = await response.json();

            setAnalysis(data);

        } catch (error) {
            console.error("Error analyzing job description:", error);
            setError("Something went wrong. Please try again.");
        } finally {
            setLoading(false);
        }
    };

  return (
      <div className="app">
        <header className="header">
          <h1>ResumeTailor AI ✨</h1>
          <p>Turn any Job Description into a resume strategy.</p>
        </header>

        <main className="container">
          <section className="input-card">
            <h2>Job Description</h2>

            <textarea
                value={jobDescription}
                onChange={(e) => setJobDescription(e.target.value)}
                placeholder="Paste the job description here..."
            />

              <button
                  onClick={handleAnalyze}
                  disabled={!jobDescription.trim() || loading}
              >
                  {loading ? (
                      <>
                          <span className="spinner"></span>
                          Analyzing...
                      </>
                  ) : (
                      "✨ Analyze Job Description"
                  )}
              </button>
              {error && (
                  <p className="error-message">
                      {error}
                  </p>
              )}
          </section>


          {analysis && (
              <section className="results">

                  <div className="results-header">
                      <h2>AI Analysis</h2>
                      <p>Here's what you should focus on for this role.</p>
                  </div>

                <div className="result-card">
                  <h2>🔑 Keywords</h2>

                  <div className="tags">
                    {analysis.keywords.map((keyword, index) => (
                        <span className="tag" key={index}>
            {keyword}
          </span>
                    ))}
                  </div>
                </div>

                <div className="result-card">
                  <h2>⭐ Must-Have Skills</h2>

                  <ul>
                    {analysis.mustHaveSkills.map((skill, index) => (
                        <li key={index}>{skill}</li>
                    ))}
                  </ul>
                </div>

                <div className="result-card">
                  <h2>💡 Nice-to-Have Skills</h2>

                  <ul>
                    {analysis.niceToHaveSkills.map((skill, index) => (
                        <li key={index}>{skill}</li>
                    ))}
                  </ul>
                </div>

                <div className="result-card">
                  <h2>📌 Resume Bullet Suggestions</h2>

                  {analysis.resumeBullets.map((bullet, index) => (
                      <div className="bullet" key={index}>
                        <p>{bullet}</p>
                          <button
                              onClick={() => handleCopy(bullet, index)}
                          >
                              {copiedIndex === index ? "✓ Copied" : "Copy"}
                          </button>
                      </div>
                  ))}
                </div>

                <div className="result-card">
                  <h2>🤖 ATS Keywords</h2>

                  <div className="tags">
                    {analysis.atsKeywords.map((keyword, index) => (
                        <span className="tag" key={index}>
            {keyword}
          </span>
                    ))}
                  </div>
                </div>

                  {analysis && (
                      <button
                          className="new-analysis-button"
                          onClick={() => {
                              setAnalysis(null);
                              setJobDescription("");
                              setError("");
                          }}
                      >
                          + Analyze Another JD
                      </button>
                  )}
              </section>
          )}

        </main>
      </div>
  );
}

export default App;