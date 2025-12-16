import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class DocumentService {
  private api = 'http://localhost:8080/api/documents';

  constructor(private http: HttpClient) {}

  uploadText(name: string, content: string) {
    return this.http.post(this.api, {
      name,
      content
    });
  }
}
