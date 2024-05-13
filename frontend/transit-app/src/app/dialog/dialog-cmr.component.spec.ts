import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogCmrComponent } from './dialog-cmr.component';

describe('DialogCmrComponent', () => {
  let component: DialogCmrComponent;
  let fixture: ComponentFixture<DialogCmrComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DialogCmrComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DialogCmrComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
