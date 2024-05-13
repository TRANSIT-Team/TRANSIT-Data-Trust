import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogOutsourceComponent } from './dialog-outsource.component';

describe('DialogOutsourceComponent', () => {
  let component: DialogOutsourceComponent;
  let fixture: ComponentFixture<DialogOutsourceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DialogOutsourceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DialogOutsourceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
