import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderPackagesComponent } from './order-packages.component';

describe('OrderPackagesComponent', () => {
  let component: OrderPackagesComponent;
  let fixture: ComponentFixture<OrderPackagesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OrderPackagesComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OrderPackagesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
