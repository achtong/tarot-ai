import { BrowserRouter, Routes, Route, Link } from "react-router-dom";
import Test from "./pages/Test";
import NotFound from "./pages/NotFound";
import TarotPage from "./pages/TarotPage";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/test" element={<Test />} />
        <Route path="/" element={<TarotPage />} />
        {/* 없는 경로로 들어왔을 때 */}
        <Route path="*" element={<NotFound />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
