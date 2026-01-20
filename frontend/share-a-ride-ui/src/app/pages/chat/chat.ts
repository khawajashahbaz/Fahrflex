import { Component, OnInit, OnDestroy, inject, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RidesApi, ChatMessage } from '../../core/api/rides-api';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './chat.html',
  styleUrls: ['./chat.scss'],
})
export class ChatComponent implements OnInit, OnDestroy {
  @ViewChild('messagesContainer') messagesContainer!: ElementRef;
  
  private api = inject(RidesApi);
  private route = inject(ActivatedRoute);
  private fb = inject(FormBuilder);
  private pollSub?: Subscription;

  rideId: string = '';
  messages: ChatMessage[] = [];
  loading = true;
  sending = false;
  
  // Current user - in real app would come from auth
  currentUserId = 'person_passenger_1';
  currentUserName = 'Anna Müller';
  
  // Other party info
  otherPartyName = 'Max Mustermann';
  otherPartyId = 'person_driver_1';
  
  // Ride details
  rideDetails = {
    departureCity: 'Neu-Ulm',
    destinationCity: 'Munich',
    departureTime: '2026-01-20T08:30:00Z'
  };

  messageForm = this.fb.group({
    message: ['']
  });

  ngOnInit(): void {
    this.rideId = this.route.snapshot.paramMap.get('rideId') || '';
    this.loadMessages();
    
    // Poll for new messages every 3 seconds
    this.pollSub = interval(3000).subscribe(() => this.loadMessages());
  }

  ngOnDestroy(): void {
    this.pollSub?.unsubscribe();
  }

  private loadMessages(): void {
    // Mock messages for demonstration
    const mockMessages: ChatMessage[] = [
      {
        id: 'msg_1',
        senderId: this.otherPartyId,
        senderName: this.otherPartyName,
        receiverId: this.currentUserId,
        content: 'Hello! I will be picking you up at Neu-Ulm Hauptbahnhof. Is that location convenient for you?',
        timestamp: '2026-01-17T10:30:00Z',
        rideId: this.rideId
      },
      {
        id: 'msg_2',
        senderId: this.currentUserId,
        senderName: this.currentUserName,
        receiverId: this.otherPartyId,
        content: 'Hi Max! Yes, that works perfectly for me. Thank you!',
        timestamp: '2026-01-17T10:32:00Z',
        rideId: this.rideId
      },
      {
        id: 'msg_3',
        senderId: this.otherPartyId,
        senderName: this.otherPartyName,
        receiverId: this.currentUserId,
        content: 'Great! I\'ll be there at 8:15 AM, so we have some buffer time before departure. My car is a black Volkswagen Golf with plate UL-AB-1234.',
        timestamp: '2026-01-17T10:35:00Z',
        rideId: this.rideId
      },
      {
        id: 'msg_4',
        senderId: this.currentUserId,
        senderName: this.currentUserName,
        receiverId: this.otherPartyId,
        content: 'Perfect, I\'ll look out for you. See you on the 20th! 🚗',
        timestamp: '2026-01-17T10:38:00Z',
        rideId: this.rideId
      }
    ];

    if (this.loading) {
      setTimeout(() => {
        this.messages = mockMessages;
        this.loading = false;
        this.scrollToBottom();
      }, 300);
    }
  }

  sendMessage(): void {
    const text = this.messageForm.value.message?.trim();
    if (!text) return;

    this.sending = true;

    const newMessage: ChatMessage = {
      id: 'msg_' + Date.now(),
      senderId: this.currentUserId,
      senderName: this.currentUserName,
      receiverId: this.otherPartyId,
      content: text,
      timestamp: new Date().toISOString(),
      rideId: this.rideId
    };

    // Simulate sending
    setTimeout(() => {
      this.messages.push(newMessage);
      this.messageForm.reset();
      this.sending = false;
      this.scrollToBottom();
    }, 300);
  }

  private scrollToBottom(): void {
    setTimeout(() => {
      if (this.messagesContainer) {
        this.messagesContainer.nativeElement.scrollTop = 
          this.messagesContainer.nativeElement.scrollHeight;
      }
    }, 50);
  }

  isOwnMessage(msg: ChatMessage): boolean {
    return msg.senderId === this.currentUserId;
  }

  formatTime(iso: string): string {
    const d = new Date(iso);
    return d.toLocaleTimeString('en-GB', {
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  formatDate(iso: string): string {
    const d = new Date(iso);
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);

    if (d.toDateString() === today.toDateString()) {
      return 'Today';
    } else if (d.toDateString() === yesterday.toDateString()) {
      return 'Yesterday';
    } else {
      return d.toLocaleDateString('en-GB', {
        day: '2-digit',
        month: 'short',
        year: 'numeric'
      });
    }
  }

  getMessageDate(msg: ChatMessage, index: number): string | null {
    const currentDate = new Date(msg.timestamp).toDateString();
    
    if (index === 0) {
      return this.formatDate(msg.timestamp);
    }
    
    const prevDate = new Date(this.messages[index - 1].timestamp).toDateString();
    
    if (currentDate !== prevDate) {
      return this.formatDate(msg.timestamp);
    }
    
    return null;
  }
}
