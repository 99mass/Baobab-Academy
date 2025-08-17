import { Link } from "react-router-dom";
import {
  ArrowRight,
  BookOpen,
  Users,
  Award,
  Search,
  Star,
  Clock,
  User,
} from "lucide-react";
import "../css/animation.css";

export default function Home() {
  return (
    <div className="min-h-screen bg-white">
      {/* Hero Section avec Image */}
      <section className="relative bg-white overflow-hidden">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid lg:grid-cols-2 gap-12 items-center py-16 lg:py-24">
            {/* Contenu texte */}
            <div className="order-2 lg:order-1 animate-fade-in-up">
              <div className="inline-flex items-center bg-success/10 text-success px-4 py-2 rounded-full text-sm font-medium mb-6 animate-bounce-in">
                <span className="w-2 h-2 bg-success rounded-full mr-2 animate-pulse"></span>
                Bienvenue sur Baobab Academy
              </div>

              <h1 className="text-4xl lg:text-5xl font-bold text-textPrimary mb-6 leading-tight animate-slide-in-left">
                Cultivez votre savoir avec
                <span className="text-primary block animate-text-gradient">
                  Baobab Academy
                </span>
              </h1>

              <p className="text-md text-gray-600 mb-8 leading-relaxed animate-fade-in opacity-0 animation-delay-200">
                Développez vos compétences professionnelles grâce à nos
                formations en ligne, conçues par des experts et adaptées à vos
                besoins.
              </p>

              <div className="flex flex-col sm:flex-row gap-4 mb-8 animate-fade-in opacity-0 animation-delay-400">
                <Link
                  to="/courses"
                  className="bg-primary text-white px-8 py-4 rounded-lg font-semibold hover:bg-primary/90 transition-all duration-300 hover:scale-105 hover:shadow-lg flex items-center justify-center space-x-2 group"
                >
                  <span>Explorer nos cours</span>
                  <ArrowRight className="w-5 h-5 group-hover:translate-x-1 transition-transform" />
                </Link>
                <Link
                  to="/auth"
                  className="border-2 border-primary text-primary px-8 py-4 rounded-lg font-semibold hover:bg-primary hover:text-white transition-all duration-300 hover:scale-105 text-center"
                >
                  Créer un compte gratuit
                </Link>
              </div>

              {/* Petites stats */}
              <div className="flex items-center space-x-6 text-sm text-gray-600 animate-fade-in opacity-0 animation-delay-600">
                <div className="flex items-center space-x-2 hover:text-primary transition-colors cursor-pointer group">
                  <Users className="w-4 h-4 group-hover:scale-110 transition-transform" />
                  <span>5000+ étudiants</span>
                </div>
                <div className="flex items-center space-x-2 hover:text-primary transition-colors cursor-pointer group">
                  <Star className="w-4 h-4 text-accent fill-current group-hover:scale-110 transition-transform" />
                  <span>4.8/5 moyenne</span>
                </div>
                <div className="flex items-center space-x-2 hover:text-primary transition-colors cursor-pointer group">
                  <BookOpen className="w-4 h-4 group-hover:scale-110 transition-transform" />
                  <span>100+ cours</span>
                </div>
              </div>
            </div>

            {/* Image Hero */}
            <div className="order-1 lg:order-2 animate-slide-in-right">
              <div className="relative">
                <img
                  src="https://images.unsplash.com/photo-1522202176988-66273c2fd55f?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1200&q=80"
                  alt="Étudiants apprenant ensemble"
                  className="rounded-2xl shadow-2xl hover:scale-105 transition-transform duration-500"
                />
                {/* Badge de lecture vidéo */}
                {/* <div className="absolute inset-0 flex items-center justify-center">
                  <button className="bg-white/90 hover:bg-white p-6 rounded-full shadow-lg transition-all duration-300 hover:scale-110 animate-pulse-slow">
                    <Play className="w-8 h-8 text-primary fill-current" />
                  </button>
                </div> */}
                {/* Petites cartes flottantes */}
                <div className="absolute -bottom-6 -left-6 bg-white p-4 rounded-xl shadow-lg animate-float">
                  <div className="flex items-center space-x-3">
                    <div className="w-10 h-10 bg-success/10 rounded-full flex items-center justify-center">
                      <Award className="w-5 h-5 text-success animate-bounce-slow" />
                    </div>
                    <div>
                      <p className="font-semibold text-sm">95% de réussite</p>
                      <p className="text-xs text-gray-500">
                        Taux de certification
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Section de recherche */}
      <section className="py-16 bg-neutral">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center animate-fade-in-up">
          <h2 className="text-2xl font-bold text-textPrimary mb-8">
            Que souhaitez-vous apprendre aujourd'hui ?
          </h2>
          <div className="relative max-w-2xl mx-auto">
            <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
            <input
              type="text"
              placeholder="Rechercher un cours, une compétence..."
              className="w-full pl-12 pr-4 py-4 border border-gray-200 rounded-xl text-lg focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent transition-all duration-300 hover:shadow-md focus:scale-105"
            />
          </div>

          {/* Tags populaires */}
          <div className="flex flex-wrap justify-center gap-3 mt-6">
            {[
              "JavaScript",
              "Python",
              "Design UX/UI",
              "Marketing Digital",
              "Data Science",
            ].map((tag, index) => (
              <span
                key={tag}
                className="px-4 py-2 bg-white text-gray-700 rounded-full text-sm font-medium hover:bg-primary hover:text-white transition-all duration-300 cursor-pointer hover:scale-105 hover:shadow-lg animate-fade-in opacity-0"
                style={{ animationDelay: `${index * 100}ms` }}
              >
                {tag}
              </span>
            ))}
          </div>
        </div>
      </section>

      {/* Cours populaires avec vraies images */}
      <section className="py-20 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12 animate-fade-in-up">
            <h2 className="text-3xl font-bold text-textPrimary mb-4">
              Cours les plus populaires
            </h2>
            <p className="text-xl text-gray-600 max-w-3xl mx-auto">
              Rejoignez des milliers d'étudiants qui font confiance à nos
              formations certifiantes.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 mb-12">
            {/* Cours 1 */}
            <a
              className="bg-white rounded-xl shadow-md hover:shadow-xl transition-all duration-300 overflow-hidden group animate-slide-in-up opacity-0 animation-delay-200 hover:scale-105"
              href="/course/1"
            >
              <div className="bg-white rounded-xl shadow-md hover:shadow-xl transition-all duration-300 overflow-hidden group">
                <div className="overflow-hidden">
                  <img
                    src="https://images.unsplash.com/photo-1516321318423-f06f85e504b3?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80"
                    alt="Développement Web"
                    className="w-full h-48 object-cover group-hover:scale-110 transition-transform duration-500"
                  />
                </div>
                <div className="p-6">
                  <div className="flex items-center space-x-2 mb-3">
                    <span className="bg-primary/10 text-primary px-3 py-1 rounded-full text-xs font-medium group-hover:bg-primary group-hover:text-white transition-colors">
                      Développement
                    </span>
                    <div className="flex items-center space-x-1">
                      <Star className="w-4 h-4 text-accent fill-current group-hover:scale-110 transition-transform" />
                      <span className="text-sm text-gray-600">4.9</span>
                    </div>
                  </div>
                  <h3 className="font-bold text-lg text-textPrimary mb-2 group-hover:text-primary transition-colors">
                    Développeur Web Full Stack
                  </h3>
                  <p className="text-gray-600 text-sm mb-4">
                    Maîtrisez JavaScript, React, Node.js et créez des
                    applications web modernes.
                  </p>
                  <div className="flex items-center justify-between">
                    <div className="flex items-center space-x-4 text-sm text-gray-500">
                      <div className="flex items-center space-x-1">
                        <Clock className="w-4 h-4" />
                        <span>6 mois</span>
                      </div>
                      <div className="flex items-center space-x-1">
                        <User className="w-4 h-4" />
                        <span>1,234 élèves</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </a>

            {/* Cours 2 */}
            <a
              className="bg-white rounded-xl shadow-md hover:shadow-xl transition-all duration-300 overflow-hidden group animate-slide-in-up opacity-0 animation-delay-400 hover:scale-105"
              href="course/2"
            >
              <div className="bg-white rounded-xl shadow-md hover:shadow-xl transition-all duration-300 overflow-hidden group">
                <div className="overflow-hidden">
                  <img
                    src="https://images.unsplash.com/photo-1611224923853-80b023f02d71?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80"
                    alt="UX Design"
                    className="w-full h-48 object-cover group-hover:scale-110 transition-transform duration-500"
                  />
                </div>
                <div className="p-6">
                  <div className="flex items-center space-x-2 mb-3">
                    <span className="bg-accent/10 text-accent px-3 py-1 rounded-full text-xs font-medium group-hover:bg-accent group-hover:text-white transition-colors">
                      Design
                    </span>
                    <div className="flex items-center space-x-1">
                      <Star className="w-4 h-4 text-accent fill-current group-hover:scale-110 transition-transform" />
                      <span className="text-sm text-gray-600">4.8</span>
                    </div>
                  </div>
                  <h3 className="font-bold text-lg text-textPrimary mb-2 group-hover:text-primary transition-colors">
                    UX/UI Designer
                  </h3>
                  <p className="text-gray-600 text-sm mb-4">
                    Concevez des expériences utilisateur exceptionnelles avec
                    Figma et les méthodes UX.
                  </p>
                  <div className="flex items-center space-x-4 text-sm text-gray-500">
                    <div className="flex items-center space-x-1">
                      <Clock className="w-4 h-4" />
                      <span>4 mois</span>
                    </div>
                    <div className="flex items-center space-x-1">
                      <User className="w-4 h-4" />
                      <span>856 élèves</span>
                    </div>
                  </div>
                </div>
              </div>
            </a>

            {/* Cours 3 */}
            <a
              className="bg-white rounded-xl shadow-md hover:shadow-xl transition-all duration-300 overflow-hidden group animate-slide-in-up opacity-0 animation-delay-600 hover:scale-105"
              href="course/3"
            >
              <div className="bg-white rounded-xl shadow-md hover:shadow-xl transition-all duration-300 overflow-hidden group">
                <div className="overflow-hidden">
                  <img
                    src="https://images.unsplash.com/photo-1551288049-bebda4e38f71?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80"
                    alt="Data Science"
                    className="w-full h-48 object-cover group-hover:scale-110 transition-transform duration-500"
                  />
                </div>
                <div className="p-6">
                  <div className="flex items-center space-x-2 mb-3">
                    <span className="bg-success/10 text-success px-3 py-1 rounded-full text-xs font-medium group-hover:bg-success group-hover:text-white transition-colors">
                      Data
                    </span>
                    <div className="flex items-center space-x-1">
                      <Star className="w-4 h-4 text-accent fill-current group-hover:scale-110 transition-transform" />
                      <span className="text-sm text-gray-600">4.7</span>
                    </div>
                  </div>
                  <h3 className="font-bold text-lg text-textPrimary mb-2 group-hover:text-primary transition-colors">
                    Data Scientist
                  </h3>
                  <p className="text-gray-600 text-sm mb-4">
                    Analysez et visualisez les données avec Python, SQL et les
                    outils de Machine Learning.
                  </p>
                  <div className="flex items-center space-x-4 text-sm text-gray-500">
                    <div className="flex items-center space-x-1">
                      <Clock className="w-4 h-4" />
                      <span>8 mois</span>
                    </div>
                    <div className="flex items-center space-x-1">
                      <User className="w-4 h-4" />
                      <span>967 élèves</span>
                    </div>
                  </div>
                </div>
              </div>
            </a>
          </div>

          <div className="text-center animate-fade-in-up">
            <Link
              to="/courses"
              className="inline-flex items-center space-x-2 bg-primary text-white px-8 py-4 rounded-lg font-semibold hover:bg-primary/90 transition-all duration-300 hover:scale-105 hover:shadow-lg group"
            >
              <span>Voir tous les cours</span>
              <ArrowRight className="w-5 h-5 group-hover:translate-x-1 transition-transform" />
            </Link>
          </div>
        </div>
      </section>

      {/* Section témoignages avec photos */}
      <section className="py-20 bg-neutral">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-16 animate-fade-in-up">
            <h2 className="text-3xl font-bold text-textPrimary mb-4">
              Ils ont réussi avec Baobab Academy
            </h2>
            <p className="text-xl text-gray-600">
              Découvrez les témoignages de nos diplômés qui ont transformé leur
              carrière.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {/* Témoignage 1 */}
            <div className="bg-white p-6 rounded-xl shadow-md hover:shadow-lg transition-all duration-300 hover:scale-105 animate-slide-in-up opacity-0 animation-delay-200">
              <div className="flex items-center space-x-4 mb-4">
                <img
                  src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?ixlib=rb-4.0.3&auto=format&fit=crop&w=100&q=80"
                  alt="Aminata Diop"
                  className="w-12 h-12 rounded-full object-cover hover:scale-110 transition-transform duration-300"
                />
                <div>
                  <p className="font-semibold text-textPrimary">Aminata Diop</p>
                  <p className="text-sm text-gray-600">Développeuse Web</p>
                </div>
              </div>
              <p className="text-gray-700 italic">
                "Grâce à Baobab Academy, j'ai pu me reconvertir dans le
                développement web. Les cours sont bien structurés et les mentors
                très disponibles."
              </p>
              <div className="flex items-center mt-4">
                {[...Array(5)].map((_, i) => (
                  <Star
                    key={i}
                    className="w-4 h-4 text-accent fill-current hover:scale-110 transition-transform"
                  />
                ))}
              </div>
            </div>

            {/* Témoignage 2 */}
            <div className="bg-white p-6 rounded-xl shadow-md hover:shadow-lg transition-all duration-300 hover:scale-105 animate-slide-in-up opacity-0 animation-delay-400">
              <div className="flex items-center space-x-4 mb-4">
                <img
                  src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?ixlib=rb-4.0.3&auto=format&fit=crop&w=100&q=80"
                  alt="Mamadou Sall"
                  className="w-12 h-12 rounded-full object-cover hover:scale-110 transition-transform duration-300"
                />
                <div>
                  <p className="font-semibold text-textPrimary">Mamadou Sall</p>
                  <p className="text-sm text-gray-600">UX Designer</p>
                </div>
              </div>
              <p className="text-gray-700 italic">
                "Une expérience formidable ! J'ai acquis toutes les compétences
                nécessaires pour devenir UX Designer et j'ai trouvé un emploi 2
                mois après ma certification."
              </p>
              <div className="flex items-center mt-4">
                {[...Array(5)].map((_, i) => (
                  <Star
                    key={i}
                    className="w-4 h-4 text-accent fill-current hover:scale-110 transition-transform"
                  />
                ))}
              </div>
            </div>

            {/* Témoignage 3 */}
            <div className="bg-white p-6 rounded-xl shadow-md hover:shadow-lg transition-all duration-300 hover:scale-105 animate-slide-in-up opacity-0 animation-delay-600">
              <div className="flex items-center space-x-4 mb-4">
                <img
                  src="https://images.unsplash.com/photo-1438761681033-6461ffad8d80?ixlib=rb-4.0.3&auto=format&fit=crop&w=100&q=80"
                  alt="Fatou Ndiaye"
                  className="w-12 h-12 rounded-full object-cover hover:scale-110 transition-transform duration-300"
                />
                <div>
                  <p className="font-semibold text-textPrimary">Fatou Ndiaye</p>
                  <p className="text-sm text-gray-600">Data Analyst</p>
                </div>
              </div>
              <p className="text-gray-700 italic">
                "Les projets pratiques m'ont permis de constituer un portfolio
                solide. Aujourd'hui, je travaille comme Data Analyst dans une
                entreprise internationale."
              </p>
              <div className="flex items-center mt-4">
                {[...Array(5)].map((_, i) => (
                  <Star
                    key={i}
                    className="w-4 h-4 text-accent fill-current hover:scale-110 transition-transform"
                  />
                ))}
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Final */}
      <section className="py-20 text-primary bg-neutral">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center animate-fade-in-up">
          <h2 className="text-xl lg:text-4xl font-bold mb-6">
            Commencez votre transformation professionnelle dès aujourd'hui
          </h2>
          <p className="text-md mb-8 text-black/70">
            Rejoignez notre communauté d'apprenants et développez les
            compétences qui feront la différence dans votre carrière.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Link
              to="/auth"
              className="bg-accent text-white/90 px-8 py-4 rounded-lg font-semibold hover:bg-accent/90 transition-all duration-300 hover:scale-105 hover:shadow-lg inline-flex items-center justify-center space-x-2 group"
            >
              <span>Créer mon compte gratuit</span>
              <ArrowRight className="w-5 h-5 group-hover:translate-x-1 transition-transform" />
            </Link>
            <Link
              to="/courses"
              className="border border-primary text-primary px-8 py-4 rounded-lg font-semibold hover:bg-primary hover:text-white transition-all duration-300 hover:scale-105 text-center"
            >
              Découvrir nos formations
            </Link>
          </div>
        </div>
      </section>
    </div>
  );
}
