import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {ChatService} from "./shared/chat.service";
import {ActivatedRoute} from "@angular/router";
import {ChatEntry} from "./shared/chat";
import {mergeMap, Observable, of, Subscription, timer, map} from "rxjs";
import {Order} from "../order/shared/order";

@Component({
  selector: 'app-chat-box',
  templateUrl: './chat-box.component.html',
  styleUrls: ['./chat-box.component.css']
})
export class ChatBoxComponent implements OnInit {
  messageValue: string = '';
  hideChatBox: boolean = true;
  chatEntries: ChatEntry[] = [];
  mSubscripton: Subscription;
  @ViewChild('chatDiv') chatDiv: ElementRef;
  @Input() order: Order;
  @Input() isParentChat: boolean = false;
  @Output() closeChat = new EventEmitter<any>();

  constructor(private chatService: ChatService, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.checkMessages();
    //this.getMessages();
    if (this.isParentChat) {
      this.hideChatBox = !this.isParentChat;
      if (!this.isParentChat) {
        this.scrollToBottomChat();
      }
    }
  }

  ngOnDestroy() {
    this.mSubscripton.unsubscribe();
  }

  changeView() {

    this.hideChatBox = !this.hideChatBox;

    if (!this.hideChatBox) {
      this.scrollToBottomChat();
    }

    if (this.isParentChat && this.hideChatBox) {
      this.closeChat.emit();
    }
  }

  checkMessages() {
    this.mSubscripton = timer(0, 5000).pipe(
      mergeMap(() => this.getMessages()),
    ).subscribe();
  }


  getMessages(): Observable<any> {
    // let orderId: any = this.route.snapshot.paramMap.get('orderId')!;
    if (this.order != undefined) {
      return this.chatService.getOrderChat(this.order.id).pipe(map((res: any) => {
        if (res._embedded != undefined) {
          this.chatEntries = res._embedded.chatentries;
          this.scrollToBottomChat();
          console.log(this.chatEntries);
        }
      }));
    }
    return of(0);
  }

  postMessage() {
    if (this.messageValue != '') {
      let orderId: any = this.order.id;
      if (orderId != undefined) {
        this.chatService.postChatMessage(orderId, this.messageValue).pipe(
          mergeMap(()=>this.getMessages())
        ).subscribe(()=>{

        });
        this.messageValue = '';

      }
    }
  }

  interval: any;

  scrollToBottomChat() {
    this.interval = setTimeout(() => {
      this.chatDiv.nativeElement.scrollTop = this.chatDiv.nativeElement.scrollHeight;
    }, 500);
  }


}
