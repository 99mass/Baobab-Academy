import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Header from "./components/Header";
import Footer from "./components/Footer";
import Home from "./pages/Home";
import Auth from "./pages/Auth";
import Courses from "./pages/Courses";
import CourseDetail from "./pages/CourseDetail";
import CoursePlayer from "./pages/CoursePlayer";
import AdminDashboard from "./pages/AdminDashboard";
import CourseEditor from "./pages/CourseEditor";
import Profile from "./pages/Profile";

function App() {
  const isAuthenticated = true; 
  const userRole = "student"; 

  return (
    <Router>
      <div className="min-h-screen bg-neutral flex flex-col">
        <Routes>
          {/* All other routes with header/footer */}
          <Route
            path="/*"
            element={
              <>
                <Header isAuthenticated={isAuthenticated} userRole={userRole} />
                <main className="flex-1">
                  <Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/auth" element={<Auth />} />
                    <Route path="/courses" element={<Courses />} />
                    <Route path="/course/:id" element={<CourseDetail />} />
                    <Route path="/player/:id" element={<CoursePlayer />} />
                    <Route
                      path="/player/:id/:lessonId"
                      element={<CoursePlayer />}
                    />
                    <Route path="/admin" element={<AdminDashboard />} />
                    <Route
                      path="/admin/course/new"
                      element={<CourseEditor />}
                    />
                    <Route
                      path="/admin/course/edit/:id"
                      element={<CourseEditor />}
                    />
                    <Route path="/profile" element={<Profile />} />
                  </Routes>
                </main>
                <Footer />
              </>
            }
          />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
