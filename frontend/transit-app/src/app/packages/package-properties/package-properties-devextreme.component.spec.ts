import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PackagePropertiesDevextremeComponent } from './package-properties-devextreme.component';

describe('PackagePropertiesDevextremeComponent', () => {
  let component: PackagePropertiesDevextremeComponent;
  let fixture: ComponentFixture<PackagePropertiesDevextremeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PackagePropertiesDevextremeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PackagePropertiesDevextremeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
