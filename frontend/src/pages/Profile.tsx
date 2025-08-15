import React, { useState } from 'react';
import { BookOpen, Clock, Award, TrendingUp, Edit } from 'lucide-react';
import CourseCard from '../components/CourseCard';
import { mockUser, mockCourses } from '../data/mockData';

export default function Profile() {
  const [activeTab, setActiveTab] = useState('overview');
  const [isEditing, setIsEditing] = useState(false);
  const [userInfo, setUserInfo] = useState({
    firstName: mockUser.firstName,
    lastName: mockUser.lastName,
    email: mockUser.email,
  });

  const enrolledCourses = mockCourses.filter(course => 
    mockUser.enrolledCourses.includes(course.id)
  );

  const completedCourses = mockCourses.filter(course => 
    mockUser.completedCourses.includes(course.id)
  );

  const inProgressCourses = enrolledCourses.filter(course => 
    course.progress && course.progress > 0 && course.progress < 100
  );

  const handleSaveProfile = () => {
    console.log('Saving profile:', userInfo);
    setIsEditing(false);
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setUserInfo({
      ...userInfo,
      [e.target.name]: e.target.value,
    });
  };

  const getCompletionRate = () => {
    if (enrolledCourses.length === 0) return 0;
    return Math.round((completedCourses.length / enrolledCourses.length) * 100);
  };

  return (
    <div className="min-h-screen bg-neutral">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Profile Header */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-8 mb-8">
          <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between">
            <div className="flex items-center space-x-6">
              {/* Avatar */}
              <div className="relative">
                <img src="./avatar.png" className="w-24 h-24" alt="" />
              </div>

              {/* User Info */}
              <div>
                {isEditing ? (
                  <div className="space-y-3">
                    <div className="flex space-x-3">
                      <input
                        type="text"
                        name="firstName"
                        value={userInfo.firstName}
                        onChange={handleInputChange}
                        className="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary transition-colors"
                        placeholder="Prénom"
                      />
                      <input
                        type="text"
                        name="lastName"
                        value={userInfo.lastName}
                        onChange={handleInputChange}
                        className="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary transition-colors"
                        placeholder="Nom"
                      />
                    </div>
                    <input
                      type="email"
                      name="email"
                      value={userInfo.email}
                      onChange={handleInputChange}
                      className="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary transition-colors"
                      placeholder="Email"
                    />
                  </div>
                ) : (
                  <>
                    <h1 className="text-2xl font-bold text-textPrimary mb-2">
                      {userInfo.firstName} {userInfo.lastName}
                    </h1>
                    <p className="text-gray-600 mb-4">{userInfo.email}</p>
                    <div className="flex items-center space-x-4 text-sm text-gray-500">
                      <span className="flex items-center space-x-2">
                        <BookOpen className="w-4 h-4" />
                        <span>{enrolledCourses.length} cours</span>
                      </span>
                      <span className="flex items-center space-x-2">
                        <Award className="w-4 h-4" />
                        <span>{completedCourses.length} terminés</span>
                      </span>
                      <span className="flex items-center space-x-2">
                        <Clock className="w-4 h-4" />
                        <span>{mockUser.totalStudyTime}h d'étude</span>
                      </span>
                    </div>
                  </>
                )}
              </div>
            </div>

            {/* Action Button */}
            <div className="mt-6 lg:mt-0">
              {isEditing ? (
                <div className="flex space-x-3">
                  <button
                    onClick={() => setIsEditing(false)}
                    className="px-6 py-3 border border-gray-300 text-textPrimary rounded-lg hover:bg-gray-50 transition-colors"
                  >
                    Annuler
                  </button>
                  <button
                    onClick={handleSaveProfile}
                    className="px-6 py-3 bg-primary text-white rounded-lg hover:bg-primary/90 transition-colors font-medium"
                  >
                    Sauvegarder
                  </button>
                </div>
              ) : (
                <button
                  onClick={() => setIsEditing(true)}
                  className="flex items-center space-x-2 px-6 py-3 border border-gray-300 text-textPrimary rounded-lg hover:bg-gray-50 transition-colors"
                >
                  <Edit className="w-5 h-5" />
                  <span>Modifier le profil</span>
                </button>
              )}
            </div>
          </div>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Cours inscrits</p>
                <p className="text-3xl font-bold text-textPrimary">{enrolledCourses.length}</p>
              </div>
              <div className="w-12 h-12 bg-primary/10 rounded-lg flex items-center justify-center">
                <BookOpen className="w-6 h-6 text-primary" />
              </div>
            </div>
          </div>

          <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Cours terminés</p>
                <p className="text-3xl font-bold text-textPrimary">{completedCourses.length}</p>
              </div>
              <div className="w-12 h-12 bg-success/10 rounded-lg flex items-center justify-center">
                <Award className="w-6 h-6 text-success" />
              </div>
            </div>
          </div>

          <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Temps d'étude</p>
                <p className="text-3xl font-bold text-textPrimary">{mockUser.totalStudyTime}h</p>
              </div>
              <div className="w-12 h-12 bg-accent/10 rounded-lg flex items-center justify-center">
                <Clock className="w-6 h-6 text-accent" />
              </div>
            </div>
          </div>

          <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Taux de réussite</p>
                <p className="text-3xl font-bold text-textPrimary">{getCompletionRate()}%</p>
              </div>
              <div className="w-12 h-12 bg-yellow-100 rounded-lg flex items-center justify-center">
                <TrendingUp className="w-6 h-6 text-yellow-600" />
              </div>
            </div>
          </div>
        </div>

        {/* Tabs */}
        <div className="flex space-x-1 mb-8 bg-white rounded-lg p-1 shadow-sm border border-gray-200 w-fit">
          <button
            onClick={() => setActiveTab('overview')}
            className={`px-6 py-3 rounded-md font-medium transition-all ${
              activeTab === 'overview'
                ? 'bg-primary text-white'
                : 'text-gray-600 hover:text-textPrimary hover:bg-gray-50'
            }`}
          >
            Vue d'ensemble
          </button>
          <button
            onClick={() => setActiveTab('inProgress')}
            className={`px-6 py-3 rounded-md font-medium transition-all ${
              activeTab === 'inProgress'
                ? 'bg-primary text-white'
                : 'text-gray-600 hover:text-textPrimary hover:bg-gray-50'
            }`}
          >
            En cours ({inProgressCourses.length})
          </button>
          <button
            onClick={() => setActiveTab('completed')}
            className={`px-6 py-3 rounded-md font-medium transition-all ${
              activeTab === 'completed'
                ? 'bg-primary text-white'
                : 'text-gray-600 hover:text-textPrimary hover:bg-gray-50'
            }`}
          >
            Terminés ({completedCourses.length})
          </button>
        </div>

        {/* Tab Content */}
        {activeTab === 'overview' && (
          <div className="space-y-8">
            {/* Continue Learning */}
            {inProgressCourses.length > 0 && (
              <div>
                <h2 className="text-2xl font-bold text-textPrimary mb-6">Continuer l'apprentissage</h2>
                <div className="space-y-4">
                  {inProgressCourses.map((course) => (
                    <CourseCard key={course.id} course={course} variant="compact" />
                  ))}
                </div>
              </div>
            )}

            {/* Recent Achievements */}
            <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-8">
              <h2 className="text-xl font-semibold text-textPrimary mb-6">Accomplissements récents</h2>
              <div className="space-y-4">
                {completedCourses.slice(0, 3).map((course) => (
                  <div key={course.id} className="flex items-center space-x-4 p-4 bg-success/5 rounded-lg">
                    <div className="w-12 h-12 bg-success rounded-full flex items-center justify-center">
                      <Award className="w-6 h-6 text-white" />
                    </div>
                    <div>
                      <h3 className="font-medium text-textPrimary">Cours terminé</h3>
                      <p className="text-sm text-gray-600">{course.title}</p>
                    </div>
                    <div className="text-right text-sm text-gray-500">
                      Il y a 2 jours
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {activeTab === 'inProgress' && (
          <div>
            <h2 className="text-2xl font-bold text-textPrimary mb-6">Cours en cours</h2>
            {inProgressCourses.length > 0 ? (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {inProgressCourses.map((course) => (
                  <CourseCard key={course.id} course={course} />
                ))}
              </div>
            ) : (
              <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-12 text-center">
                <BookOpen className="w-16 h-16 text-gray-400 mx-auto mb-4" />
                <h3 className="text-lg font-medium text-textPrimary mb-2">Aucun cours en cours</h3>
                <p className="text-gray-600">Explorez nos cours pour commencer votre apprentissage</p>
              </div>
            )}
          </div>
        )}

        {activeTab === 'completed' && (
          <div>
            <h2 className="text-2xl font-bold text-textPrimary mb-6">Cours terminés</h2>
            {completedCourses.length > 0 ? (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {completedCourses.map((course) => (
                  <CourseCard key={course.id} course={course} />
                ))}
              </div>
            ) : (
              <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-12 text-center">
                <Award className="w-16 h-16 text-gray-400 mx-auto mb-4" />
                <h3 className="text-lg font-medium text-textPrimary mb-2">Aucun cours terminé</h3>
                <p className="text-gray-600">Terminez vos premiers cours pour voir vos accomplissements ici</p>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}