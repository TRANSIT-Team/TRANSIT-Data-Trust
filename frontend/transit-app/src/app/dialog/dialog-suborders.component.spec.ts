import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogSubordersComponent } from './dialog-suborders.component';

describe('DialogSubordersComponent', () => {
  let component: DialogSubordersComponent;
  let fixture: ComponentFixture<DialogSubordersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DialogSubordersComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DialogSubordersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
