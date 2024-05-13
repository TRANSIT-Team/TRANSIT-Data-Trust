import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogPackagePropertiesComponent } from './dialog-package-properties.component';

describe('DialogPackagePropertiesComponent', () => {
  let component: DialogPackagePropertiesComponent;
  let fixture: ComponentFixture<DialogPackagePropertiesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DialogPackagePropertiesComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DialogPackagePropertiesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
