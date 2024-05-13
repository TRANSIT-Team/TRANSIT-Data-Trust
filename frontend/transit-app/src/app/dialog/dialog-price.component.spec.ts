import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogPriceComponent } from './dialog-price.component';

describe('DialogPriceComponent', () => {
  let component: DialogPriceComponent;
  let fixture: ComponentFixture<DialogPriceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DialogPriceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DialogPriceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
