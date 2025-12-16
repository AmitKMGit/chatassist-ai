import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ConversationService } from '../../services/conversation.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.html'
})
export class Chat implements OnInit  {

  
  conversations: any[] = [];
  messages: any[] = [];
  selectedId!: number;
  input = '';
  constructor(private convService: ConversationService,private router: Router) {}
ngOnInit() {
    this.convService.getAll().subscribe(res => {
      this.conversations = res;
      if (res.length) {
        this.select(res[0].id);
      }
    });
  }
  select(id: number) {
    this.selectedId = id;
    this.convService.getMessages(id).subscribe(res => {
      this.messages = res;
    });
  }


 send() {
    if (!this.input.trim()) return;

    const text = this.input;
    this.input = '';

    this.convService.sendMessage(this.selectedId, text)
      .subscribe(res => this.messages = res);
  }
  
logout() {
  localStorage.removeItem('token');
  this.router.navigate(['/login']);
}

}
