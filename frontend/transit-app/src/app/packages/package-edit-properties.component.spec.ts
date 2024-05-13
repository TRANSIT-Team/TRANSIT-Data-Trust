import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PackageEditPropertiesComponent } from './package-edit-properties.component';

describe('PackageEditPropertiesComponent', () => {
  let component: PackageEditPropertiesComponent;
  let fixture: ComponentFixture<PackageEditPropertiesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PackageEditPropertiesComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PackageEditPropertiesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
