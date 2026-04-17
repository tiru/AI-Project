"use client";
import React, { createContext, useContext, useEffect, useState } from "react";

interface AuthUser { username: string; role: string; token: string }
interface AuthCtx {
  user: AuthUser | null;
  setUser: (u: AuthUser | null) => void;
  logout: () => void;
  isAdmin: () => boolean;
  isOperator: () => boolean;
}

const AuthContext = createContext<AuthCtx>({
  user: null, setUser: () => {}, logout: () => {},
  isAdmin: () => false, isOperator: () => false,
});

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUserState] = useState<AuthUser | null>(null);

  useEffect(() => {
    const token    = localStorage.getItem("token");
    const username = localStorage.getItem("username");
    const role     = localStorage.getItem("role");
    if (token && username && role) setUserState({ token, username, role });
  }, []);

  const setUser = (u: AuthUser | null) => {
    setUserState(u);
    if (u) {
      localStorage.setItem("token",    u.token);
      localStorage.setItem("username", u.username);
      localStorage.setItem("role",     u.role);
    } else {
      localStorage.removeItem("token");
      localStorage.removeItem("username");
      localStorage.removeItem("role");
    }
  };

  const logout = () => setUser(null);

  return (
    <AuthContext.Provider value={{
      user, setUser, logout,
      isAdmin:    () => user?.role === "ADMIN",
      isOperator: () => user?.role === "ADMIN" || user?.role === "OPERATOR",
    }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);