import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderCommentsPublicComponent } from './order-comments-public.component';

describe('OrderCommentsPublicComponent', () => {
  let component: OrderCommentsPublicComponent;
  let fixture: ComponentFixture<OrderCommentsPublicComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OrderCommentsPublicComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OrderCommentsPublicComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
