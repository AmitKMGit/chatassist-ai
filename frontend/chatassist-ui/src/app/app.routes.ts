import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Chat } from './pages/chat/chat';
import { Documents } from './pages/documents/documents';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
    { path: 'login', component: Login },
  { path: 'chat', component: Chat ,canActivate: [authGuard]},
  { path: 'documents', component: Documents ,canActivate: [authGuard] }
,
  { path: '', redirectTo: 'login', pathMatch: 'full' }
];
