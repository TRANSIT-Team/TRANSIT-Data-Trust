import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {ChatService} from "./shared/chat.service";
import {ResourceCollection} from "@lagoshny/ngx-hateoas-client";
import {ChatEntry} from "./shared/chat";
import {map, mergeMap, Observable, Subscription, timer} from "rxjs";
import {OrderService} from "../order/shared/order.service";
import {Order} from "../order/shared/order";


@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})

export class ChatComponent implements OnInit {

  @ViewChild('chatDiv') chatDiv: ElementRef;

  currentOrderId = '';
  currentOrderLinkId = '';
  chatSummaries: ChatEntry[] = [];
  filteredChatSummaries: ChatEntry[] = [];
  loading = true;
  filtered = false;
  chatEntries: ChatEntry[] = [];
  messageValue = '';
  searchValue = '';
  activeChat = false;
  mSubscripton: Subscription;
  currentChat: ChatEntry;
  currentChatLoading = false;

  constructor(private chatService: ChatService, private orderService: OrderService) {
  }

  ngOnInit(): void {
    //this.getChatOverview().subscribe();
    this.checkMessages();
   // this.checkMessagesManual();
  }

  ngOnDestroy() {
    this.mSubscripton.unsubscribe();
  }

  checkMessages() {
    this.mSubscripton = timer(0, 10000).pipe(
      mergeMap(() => this.getChatOverview()),
    ).subscribe();
  }


  checkMessagesManual() {
    this.getChatOverview().subscribe();
  }

  getChatOverview(): Observable<any> {
    return this.chatService.getChatOverview().pipe(
      map((coll: ChatEntry[]) => {

        this.chatSummaries = coll;


        this.chatSummaries.forEach((o: ChatEntry) => {
          o.orderShortId = '#';
          if (o.orderId != undefined) {
            const parts = o.orderId.split('-');
            o.orderShortId = '#' + parts[0];
          }
        });


        if (!this.filtered) {
          this.filteredChatSummaries = this.chatSummaries;
          if (this.currentChat != undefined) {

            let filtercurrentChat = this.filteredChatSummaries.filter((message) => message.id == this.currentChat.id)[0];

            if (filtercurrentChat) {
              filtercurrentChat.active = true;

            }

          }
        }
        this.loading = false;
      }));
  }

  clearFilterChatSummaries() {
    this.searchValue = '';
    this.filtered = false;
    this.filteredChatSummaries = this.chatSummaries;

  }

  filterChatSummaries() {
    this.filteredChatSummaries = this.chatSummaries.filter((message) => message.orderId!.includes(this.searchValue));
    this.filtered = true;

  }

  activateChat(chat: any) {
    this.currentChatLoading = true;
    this.filteredChatSummaries.forEach((m) => {
      m.active = false;
    });
    this.currentChat = this.filteredChatSummaries.filter((message) => message.id == chat.id)[0];
    this.currentChat.active = true;
    this.activeChat = true;
    this.getMessages(chat.orderId);
    this.getParentOrderId(chat.orderId);


  }

  getMessages(orderId: any) {
    if (orderId != undefined) {
      this.currentOrderId = orderId;
      this.chatService.getOrderChat(orderId).subscribe((res: any) => {
        this.chatEntries = res._embedded.chatentries;
        this.postReadStatus();
        this.currentChatLoading = false;
        this.scrollToBottomChat();
      });
    }
  }

  getParentOrderId(orderId: any) {
    if (orderId != undefined) {
      this.currentOrderLinkId = '...';
      this.orderService.getOrder(orderId).pipe(
        map((res: any) => {
          console.log(res);
          if (!res.isMyOrder) {
            this.currentOrderLinkId = res.parentOrderId.id;
          } else {
            this.currentOrderLinkId = orderId;
          }
        })
      ).subscribe();
    }
  }

  postMessage() {
    if (this.messageValue != '') {
      if (this.currentOrderId != undefined) {

        let m: any = this.messageValue;
        this.messageValue = '';
        this.chatService.postChatMessage(this.currentOrderId, m).pipe(
          map(() => {

          }),
          mergeMap(() => this.chatService.getOrderChat(this.currentOrderId)),
          map((res: any) => {
            this.chatEntries = res._embedded.chatentries;
            this.postReadStatus();
            this.currentChatLoading = false;
            this.scrollToBottomChat();
            this.getMessages(this.currentOrderId);
            this.checkMessagesManual();
          })
        ).subscribe();

      }
    }
  }


  interval: any;

  scrollToBottomChat() {
    this.interval = setTimeout(() => {
      this.chatDiv.nativeElement.scrollTop = this.chatDiv.nativeElement.scrollHeight;
    }, 500);
  }

  postReadStatus() {
    let updateStatus: boolean = false;
    this.chatEntries.forEach((chatEntry: ChatEntry) => {
      if (chatEntry.readStatus == false && chatEntry.person == 'YOU') {
        updateStatus = true;
      }
    });

    console.log("asdsd");
    if (updateStatus) {
      this.chatService.postChatMessageReadStatus(this.currentOrderId).subscribe();
      this.checkMessagesManual();

      let filtercurrentChat = this.filteredChatSummaries.filter((message) => message.id == this.currentChat.id)[0];

      if (filtercurrentChat) {
        filtercurrentChat.readStatus = true;
      }


    }
  }
}
