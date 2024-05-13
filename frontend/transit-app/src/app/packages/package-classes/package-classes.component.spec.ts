import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PackageClassesComponent } from './package-classes.component';

describe('PackageClassesComponent', () => {
  let component: PackageClassesComponent;
  let fixture: ComponentFixture<PackageClassesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PackageClassesComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PackageClassesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
