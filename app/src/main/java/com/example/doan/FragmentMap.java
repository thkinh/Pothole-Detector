package com.example.doan;


import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.addOnMapClickListener;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.getGestures;
import static com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils.getLocationComponent;
import static com.mapbox.navigation.base.extensions.RouteOptionsExtensions.applyDefaultNavigationOptions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.doan.interfaceFragment.OnMapFragmentInteractionListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;


import com.mapbox.geojson.Feature;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;


import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor;
import com.mapbox.maps.extension.style.sources.SourceUtils;
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.gestures.OnMapClickListener;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;
import com.mapbox.maps.viewannotation.ViewAnnotationManager;
import com.mapbox.search.autocomplete.PlaceAutocomplete;
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion;
import com.mapbox.search.ui.adapter.autocomplete.PlaceAutocompleteUiAdapter;
import com.mapbox.search.ui.view.CommonSearchViewConfiguration;
import com.mapbox.search.ui.view.SearchResultsView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentMap extends Fragment
        implements PermissionsListener , OnMapReadyCallback {
    private MapView mapView;
    private PermissionsManager permissionsManager;

    //Các thành phần của layout
    private FloatingActionButton navigationButton;
    private FloatingActionButton mylocationButton;
    private PlaceAutocomplete placeAutocomplete;
    private PlaceAutocompleteUiAdapter placeAutocompleteUiAdapter;
    private SearchResultsView searchResultsView;
    private TextInputEditText searchET;
    private boolean ignoreNextQueryUpdate = false;
    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";


    public FragmentMap() {
        super(R.layout.fragment_map);
    }

    private final OnIndicatorPositionChangedListener onIndicatorPositionChangedListener = new OnIndicatorPositionChangedListener() {
        @Override
        public void onIndicatorPositionChanged(@NonNull Point point) {
            mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point).zoom(15.0).build());
            getGestures(mapView).setFocalPoint(mapView.getMapboxMap().pixelForCoordinate(point));
        }
    };
    private final OnMoveListener onMoveListener = new OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
            getLocationComponent(mapView).removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
            getGestures(mapView).removeOnMoveListener(onMoveListener);
            mylocationButton.show();
        }

        @Override
        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
            return false;
        }

        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {

        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView) view.findViewById(R.id.mapbox);
        mylocationButton = (FloatingActionButton) view.findViewById(R.id.mylocationButton);
        mylocationButton.hide();
        navigationButton = (FloatingActionButton) view.findViewById(R.id.navigationButton);
        searchET = (TextInputEditText) view.findViewById(R.id.searchET);
        searchResultsView = (SearchResultsView) view.findViewById(R.id.search_results_view);
        return view;
    }

    private OnMapFragmentInteractionListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnMapFragmentInteractionListener) {
            listener = (OnMapFragmentInteractionListener) context;
        }
    }

    //để hiển thị vị trí hiện tại và các thành phần của vị trí Mapbox thông qua location component
    //thông qua thư viện location của mapbox
    //nhưng ở đây sử dụng thư viện LocationComponentPlugin để kích hoạt vị trí người dùng
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        placeAutocomplete = PlaceAutocomplete.create(getString(R.string.mapbox_access_token));
        searchResultsView.initialize(new SearchResultsView.Configuration(new CommonSearchViewConfiguration()));
        placeAutocompleteUiAdapter = new PlaceAutocompleteUiAdapter(searchResultsView, placeAutocomplete, LocationEngineProvider.getBestLocationEngine(getContext()));

        navigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMapButtonClicked(new FragmentMapNavigation());
            }
        });

        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {


                setMylocationButton();
                setSearchET();


                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_location_pin);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 90, 90, true);
                AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
                PointAnnotationManager pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);
                ViewAnnotationManager viewAnnotationManager = mapView.getViewAnnotationManager();

                addOnMapClickListener(mapView.getMapboxMap(), new OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull Point point) {
                        pointAnnotationManager.deleteAll();
                        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions().withTextAnchor(TextAnchor.CENTER).withIconImage(resizedBitmap)
                                .withPoint(point);
                        pointAnnotationManager.create(pointAnnotationOptions);
                        fetchDirection(point);
                        return false;
                    }
                });

            }
        });

    }


    private PermissionsListener permissionsListener;

    //Lắng nghe các quyền cần để sử dụng cho ứng dụng
    @Override
    public void onExplanationNeeded(@NonNull List<String> list) {

    }

    //Kết quả cấp phép được gọi khi người dùng có cho phép hoặc từ chối cấp phép hay không, xử lí hành động sau khi ứng dụng yêu cầu cấp phép tiếp theo
    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {

        } else {

        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_location_pin);
        AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
        PointAnnotationManager pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);
        //Đặt địa chỉ đích để vẽ đường đi
        addOnMapClickListener(mapView.getMapboxMap(), new OnMapClickListener() {
            @Override
            public boolean onMapClick(@NonNull Point point) {

                addMarkerDestination(point);
                return true;
            }
        });


    }


    public void setMylocationButton() {
        mapView.getMapboxMap().setCamera(new CameraOptions.Builder().zoom(10.0).build());
        LocationComponentPlugin locationComponentPlugin = getLocationComponent(mapView);
        locationComponentPlugin.setEnabled(true);
        locationComponentPlugin.setPulsingEnabled(true);
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
        getGestures(mapView).addOnMoveListener(onMoveListener);

        mylocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
                getGestures(mapView).addOnMoveListener(onMoveListener);
                mylocationButton.hide();
            }
        });
    }

    public void setSearchET() {
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (ignoreNextQueryUpdate) {
                    ignoreNextQueryUpdate = false;
                } else {
                    placeAutocompleteUiAdapter.search(charSequence.toString(), new Continuation<Unit>() {
                        @NonNull
                        @Override
                        public CoroutineContext getContext() {
                            return EmptyCoroutineContext.INSTANCE;
                        }

                        @Override
                        public void resumeWith(@NonNull Object o) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    searchResultsView.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        placeAutocompleteUiAdapter.addSearchListener(new PlaceAutocompleteUiAdapter.SearchListener() {
            @Override
            public void onSuggestionsShown(@NonNull List<PlaceAutocompleteSuggestion> list) {

            }

            @Override
            public void onSuggestionSelected(@NonNull PlaceAutocompleteSuggestion placeAutocompleteSuggestion) {
                ignoreNextQueryUpdate = true;
                searchET.setText(placeAutocompleteSuggestion.getName());
                searchResultsView.setVisibility(View.GONE);
            }

            @Override
            public void onPopulateQueryClick(@NonNull PlaceAutocompleteSuggestion placeAutocompleteSuggestion) {

            }

            @Override
            public void onError(@NonNull Exception e) {
                Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Chưa chạy được
    public void addMarkerDestination(Point point) {

    }

    @SuppressLint("MissingPermission")
    private void fetchDirection(Point destination) {
        Log.d("FetchDirection", "fetchDirection() started"); // Log khi bắt đầu hàm

        LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(getContext());
        Log.d("FetchDirection", "LocationEngine initialized");

        locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult result) {
                Log.d("FetchDirection", "Location fetch succeeded");
                Location location = result.getLastLocation();

                if (location == null) {
                    Log.e("FetchDirection", "Error: Location is null");
                    Toast.makeText(getContext(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("FetchDirection", "Location details: Lat=" + location.getLatitude() + ", Lng=" + location.getLongitude());
                Point origin = Point.fromLngLat(location.getLongitude(), location.getLatitude());

                if (origin == null || destination == null) {
                    Log.e("FetchDirection", "Error: Origin or Destination is null");
                    return;
                }


                MapboxDirections.Builder builder = MapboxDirections.builder();
                RouteOptions routeOptions = RouteOptions.builder()
                        .profile(DirectionsCriteria.PROFILE_DRIVING_TRAFFIC)
                        .geometries(DirectionsCriteria.GEOMETRY_POLYLINE6)
                        .coordinatesList(Arrays.asList(origin, destination))
                        .waypointsPerRoute(true)
                        .alternatives(true)
                        .build();
                Log.d("FetchDirection", "RouteOptions built successfully");

                builder.routeOptions(routeOptions);
                builder.accessToken(getString(R.string.mapbox_access_token));

                builder.build().enqueueCall(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        DirectionsResponse directionsResponse = response.body();
                        DirectionsRoute currentRoute = directionsResponse.routes().get(0);

                        // Make a toast which displays the route's distance
                        Toast.makeText(getContext(), String.format("Route distance: %.2f meters", currentRoute.distance()), Toast.LENGTH_SHORT).show();

//                        if (mapView != null) {
////                                mapView.getMapboxMap().lo(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
//                            mapView.getMapboxMap().loadStyle(new Style.OnStyleLoaded() {
//                                @Override
//                                public void onStyleLoaded(@NonNull Style style) {
//                                    // Create a list to store our line coordinates.
//                                    // Retrieve and update the source designated for showing the directions route
//
//
//
//
//                                }
//                            });
//                        }
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }
}
