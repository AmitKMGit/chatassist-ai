import { CanActivateFn } from '@angular/router';

export const authGuard: CanActivateFn = () => {
  if (typeof window === 'undefined') {
    return false;
  }

  const token = window.localStorage.getItem('token');
  return !!token;
};
