import { Component } from '@angular/core';
import { DocumentService } from '../../services/document.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-documents',
   standalone: true,
  imports: [FormsModule,CommonModule],
  templateUrl: './documents.html',
  styleUrl: './documents.scss',
})
export class Documents {
  name = '';
  content = '';
  status = '';

  constructor(private docService: DocumentService) {}

  upload() {
    if (!this.name || !this.content) return;

    this.docService.uploadText(this.name, this.content).subscribe({
      next: () => this.status = 'Uploaded successfully',
      error: () => this.status = 'Upload failed'
    });
  }

}
