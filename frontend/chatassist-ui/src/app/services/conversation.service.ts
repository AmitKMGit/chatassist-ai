import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class ConversationService {
  private api = 'http://localhost:8080/api/conversations';

  constructor(private http: HttpClient) {}

  getAll() {
    return this.http.get<any[]>(this.api);
  }

  getMessages(id: number) {
    return this.http.get<any[]>(`${this.api}/${id}/messages`);
  }

  sendMessage(id: number, content: string) {
    return this.http.post<any[]>(
      `${this.api}/${id}/messages`,
      { content }
    );
  }
}
